package kr.yangbob.memoapp.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
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
import androidx.lifecycle.Observer
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
    private val model: CrudViewModel by viewModel()
    private lateinit var dialogForBackBtn: AlertDialog
    private lateinit var dialogForInputUrl: AlertDialog
    private lateinit var imm: InputMethodManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityCrudBinding>(this, R.layout.activity_crud)
        binding.lifecycleOwner = this
        binding.model = model

        val memoId: Int = intent.getIntExtra("memoId", -1)
        if (memoId > 0) {
            toolbar.setTitle(R.string.crud_appbar_title_detail)
            model.getMemo(memoId)
        } else {
            toolbar.setTitle(R.string.crud_appbar_title_add)
            editTitle.requestFocus()
        }

        setSupportActionBar(toolbar)


        val imageAdapter = ImageListAdapter()
        imageRecycler.adapter = imageAdapter
        imageRecycler.setHasFixedSize(false)

        model.getImageList().observe(this, Observer {
            imageAdapter.updateList(it.toList())
        })


        writeLayoutLinear.setOnClickListener {
            editBody.requestFocus()
        }
        val focusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus && model.isDetailMode()) changeModeTo(Mode.Edit, v)
        }
        editTitle.onFocusChangeListener = focusChangeListener
        editBody.onFocusChangeListener = focusChangeListener

        // lateinit variable init
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
        val inputUrl = inputUrlLayout.findViewById<EditText>(R.id.inputUrl)
        dialogForInputUrl = AlertDialog.Builder(this)
                .setMessage(R.string.crud_url_dialog_msg)
                .setView(inputUrlLayout)
                .setPositiveButton(R.string.crud_url_dialog_positive) { _, _ ->
                    val url = inputUrl.text.toString()
                    inputUrl.setText("")

                    if (URLUtil.isHttpsUrl(url)) {
                        model.addPicture(url)
                    } else if (URLUtil.isHttpUrl(url)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                            Toast.makeText(this, R.string.crud_url_dialog_deny_http_msg, Toast.LENGTH_LONG).show()
                        else model.addPicture(url)
                    } else {
                        Toast.makeText(this, R.string.crud_url_dialog_invalid, Toast.LENGTH_LONG).show()
                    }
                }
                .setNegativeButton(R.string.crud_url_dialog_negative) { _, _ -> }
                .create()
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
            model.deleteMemo()
            finish()
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
            if (isNotSave) model.resetData()
            changeModeTo(Mode.Detail)
            imm.hideSoftInputFromWindow(editTitle.windowToken, 0)
        }
    }

    // image list 삭제버튼 toggle 기능 추가 필요
    private fun changeModeTo(mode: Mode, focusView: View? = null) {
        if (mode == Mode.Add) throw IllegalArgumentException()
        model.changeMode(mode)
        if (mode == Mode.Detail) {
            toolbar.setTitle(R.string.crud_appbar_title_detail)
            editTitle.clearFocus()
            editBody.clearFocus()
        } else {
            toolbar.setTitle(R.string.crud_appbar_title_edit)
            val editText: EditText = if (focusView == null) editTitle
            else focusView as EditText

            editText.requestFocus()
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }
        invalidateOptionsMenu()
    }

    fun removePicture(uri: String) {
        model.removePicture(uri)
    }
}
