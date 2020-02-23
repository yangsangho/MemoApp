package kr.yangbob.memoapp.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.URLUtil
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_crud.*
import kr.yangbob.memoapp.Mode
import kr.yangbob.memoapp.R
import kr.yangbob.memoapp.checkPermissionAndRun
import kr.yangbob.memoapp.databinding.ActivityCrudBinding
import kr.yangbob.memoapp.viewmodel.CrudViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.IOException

class CrudActivity : AppCompatActivity() {
    private val requestCodeGallery = 1
    private val requestCodeCamera = 2
    private val requestBigImageActivity = 3

    private val model: CrudViewModel by viewModel()
    private val loadFailListForUrl = mutableListOf<String>()
    lateinit var canDelete: LiveData<Boolean>
    private lateinit var imageList: LiveData<MutableList<String>>

    private lateinit var imageListAdapter: ImageListAdapter
    private lateinit var dialogForBackBtn: AlertDialog
    private lateinit var dialogForInputUrl: AlertDialog
    private lateinit var dialogForEditUrl: AlertDialog
    private lateinit var dialogForDelete: AlertDialog
    private lateinit var imm: InputMethodManager
    private lateinit var urlEditText: EditText
    private var urlEditIdx = 0

    private val focusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
        if (hasFocus && model.isDetailMode()) changeModeTo(Mode.Edit, v)
        (v as EditText).also {
            it.setSelection(it.text.length)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityCrudBinding>(this, R.layout.activity_crud)
        binding.lifecycleOwner = this
        binding.model = model

        canDelete = model.getCanDelete()
        imageList = model.getImageList()

        val memoId: Int = intent.getIntExtra("memoId", -1)
        model.getMemo(memoId)
        if (memoId > 0) {
            toolbar.setTitle(R.string.crud_appbar_title_detail)
        } else {
            toolbar.setTitle(R.string.crud_appbar_title_add)
            editTitle.requestFocus()
        }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        imageListAdapter = ImageListAdapter(this)
        imageRecycler.adapter = imageListAdapter
        imageList.observe(this, Observer {
            imageListAdapter.updateList(it.toList())
            if (model.lastActionIsAdd) {
                if (it.size > 1) {
                    imageRecycler.layoutManager?.scrollToPosition(it.size - 1)
                }
            }
        })

        writeLayoutLinear.setOnClickListener {
            editBody.requestFocus()
        }
        editTitle.onFocusChangeListener = focusChangeListener
        editBody.onFocusChangeListener = focusChangeListener

        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        createDialog()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(model.getMenuId(), menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_gallery -> {
            val mimeTypes = arrayOf("image/jpeg", "image/png")
            checkPermissionAndRun(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    R.string.permission_gallery_rationale,
                    R.string.permission_gallery_denied) {
                startActivityForResult(Intent().apply {
                    action = Intent.ACTION_PICK
                    type = MediaStore.Images.Media.CONTENT_TYPE
                    putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                }, requestCodeGallery)
            }
            true
        }
        R.id.action_camera -> {
            checkPermissionAndRun(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                    R.string.permission_camera_rationale,
                    R.string.permission_camera_denied) {
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).let { intent ->
                    val photoFile: File? = try {
                        model.createImageFile()
                    } catch (ex: IOException) {
                        null
                    }
                    photoFile?.let {
                        val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", it)
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                        startActivityForResult(intent, requestCodeCamera)
                    }
                }
            }
            true
        }
        R.id.action_url -> {
            dialogForInputUrl.show()
            true
        }
        R.id.action_save -> {
            var isNotSave = true
            if (model.hasChange()) {
                isNotSave = false
                model.saveData()
            }
            if (isNotSave && model.isAddMode()) Toast.makeText(this, R.string.crud_dont_save_msg, Toast.LENGTH_LONG).show()
            processAfterSave(isNotSave)
            true
        }
        R.id.action_edit -> {
            changeModeTo(Mode.Edit)
            true
        }
        R.id.action_delete -> {
            dialogForDelete.show()
            true
        }
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                requestCodeGallery -> {
                    data?.data?.let { uri ->
                        model.addPicture(uri)
                    }
                }
                requestCodeCamera -> {
                    model.saveCameraImage()
                }
                requestBigImageActivity -> {
                    data?.getIntArrayExtra("deleteIdxList")?.also {
                        it.forEach { deleteIdx -> model.removePicture(deleteIdx) }
                    }
                    data?.getIntExtra("idx", 0)?.also {
                        imageRecycler.layoutManager?.scrollToPosition(it)
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (model.hasChange()) dialogForBackBtn.show()
        else {
            if (model.isEditMode()) changeModeTo(Mode.Detail)
            else super.onBackPressed()
        }
    }

    private fun processAfterSave(isNotSave: Boolean) {
        if (model.isAddMode()) {
            finish()
        } else {
            if (isNotSave) {
                model.resetData()
                imageRecycler.layoutManager?.scrollToPosition(0)
            }
            changeModeTo(Mode.Detail)
        }
    }

    private fun changeModeTo(mode: Mode, focusView: View? = null) {
        if (mode == Mode.Add) throw IllegalArgumentException()
        model.changeMode(mode)
        if (mode == Mode.Detail) {
            toolbar.setTitle(R.string.crud_appbar_title_detail)
            editTitle.clearFocus()
            editBody.clearFocus()
            imm.hideSoftInputFromWindow(editTitle.windowToken, 0)
        } else {
            toolbar.setTitle(R.string.crud_appbar_title_edit)
            val editText: EditText = if (focusView == null) editTitle
            else focusView as EditText

            editText.requestFocus()
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }
        invalidateOptionsMenu()
    }


    private fun createDialog() {
        dialogForBackBtn = AlertDialog.Builder(this)
                .setMessage(R.string.crud_chk_dialog_msg)
                .setPositiveButton(R.string.crud_chk_dialog_positive) { _, _ ->
                    model.saveData()
                    processAfterSave(isNotSave = false)
                }
                .setNegativeButton(R.string.crud_chk_dialog_negative) { _, _ ->
                    processAfterSave(isNotSave = true)
                }
                .create()


        val inputUrlLayout = layoutInflater.inflate(R.layout.dialog_input_url, null)
        val urlInputText = inputUrlLayout.findViewById<EditText>(R.id.inputUrl)
        dialogForInputUrl = AlertDialog.Builder(this)
                .setMessage(R.string.crud_url_dialog_msg)
                .setView(inputUrlLayout)
                .setPositiveButton(R.string.crud_url_dialog_positive) { _, _ ->
                    var url = urlInputText.text.toString()
                    urlInputText.setText("")

                    if(!URLUtil.isHttpsUrl(url) && !URLUtil.isHttpUrl(url)){
                       url = "https://$url"
                    }

                    if(Patterns.WEB_URL.matcher(url).matches()){
                        if (URLUtil.isHttpsUrl(url)) {
                            model.addPicture(url)
                        } else if (URLUtil.isHttpUrl(url)) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                                Toast.makeText(this, R.string.crud_url_dialog_deny_http_msg, Toast.LENGTH_LONG).show()
                            else model.addPicture(url)
                        }
                    } else{
                        Toast.makeText(this, R.string.crud_url_dialog_invalid, Toast.LENGTH_LONG).show()
                    }
                }
                .setNegativeButton(R.string.crud_url_dialog_negative) { _, _ -> }.create()

        val editUrlLayout = layoutInflater.inflate(R.layout.dialog_input_url, null)
        urlEditText = editUrlLayout.findViewById(R.id.inputUrl)
        dialogForEditUrl = AlertDialog.Builder(this)
                .setMessage(R.string.crud_url_dialog_edit_msg)
                .setView(editUrlLayout)
                .setPositiveButton(R.string.crud_url_dialog_edit_positive) { _, _ ->
                    var url = urlEditText.text.toString()

                    if(!URLUtil.isHttpsUrl(url) && !URLUtil.isHttpUrl(url)){
                        url = "https://$url"
                    }

                    if(Patterns.WEB_URL.matcher(url).matches()){
                        if (URLUtil.isHttpsUrl(url)) {
                            model.modifyUrl(urlEditIdx, url)
                        } else if (URLUtil.isHttpUrl(url)) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                                Toast.makeText(this, R.string.crud_url_dialog_deny_http_msg, Toast.LENGTH_LONG).show()
                            else model.modifyUrl(urlEditIdx, url)
                        }
                    } else{
                        Toast.makeText(this, R.string.crud_url_dialog_invalid, Toast.LENGTH_LONG).show()
                    }
                }
                .setNegativeButton(R.string.crud_url_dialog_negative) { _, _ -> }.create()

        dialogForDelete = AlertDialog.Builder(this)
                .setMessage(R.string.crud_delete_dialog_msg)
                .setPositiveButton(R.string.crud_delete_dialog_positive) { _, _ ->
                    model.deleteMemo()
                    finish()
                }
                .setNegativeButton(R.string.crud_delete_dialog_negative) { _, _ -> }.create()
    }

    fun removePicture(idx: Int) {
        model.removePicture(idx)
    }

    fun startBigImage(idx: Int) {
        imageList.value?.also {
            if(loadFailListForUrl.contains(it[idx])){
                urlEditText.setText(it[idx])
                urlEditIdx = idx
                dialogForEditUrl.show()
            } else {
                startActivityForResult(Intent(this, BigImageActivity::class.java).apply {
                    putExtra("imageList", it.toTypedArray())
                    putExtra("idx", idx)
                    putExtra("isDetailMode", model.isDetailMode())
                }, requestBigImageActivity)
            }
        }
    }

    fun imageLoadFail(uri: String) {
        if (URLUtil.isContentUrl(uri)) {
            model.removePicture(uri)
        } else {
            loadFailListForUrl.add(uri)
        }
        if (model.isDetailMode()) {
            if (model.hasChange()) model.saveData()
        }
    }

    override fun onDestroy() {
        Glide.get(this).clearMemory()
        super.onDestroy()
    }
}
