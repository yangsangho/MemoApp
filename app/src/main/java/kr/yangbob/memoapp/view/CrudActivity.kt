package kr.yangbob.memoapp.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_crud.*
import kr.yangbob.memoapp.R
import kr.yangbob.memoapp.databinding.ActivityCrudBinding
import kr.yangbob.memoapp.viewmodel.CrudViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.IOException


class CrudActivity : AppCompatActivity() {
    private val model: CrudViewModel by viewModel()
    private val REQUEST_CODE_GALLERY = 1
    private val REQUEST_CODE_CAMERA = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityCrudBinding>(this, R.layout.activity_crud)
        binding.lifecycleOwner = this
        binding.model = model

        val memoId: Int = intent.getIntExtra("memoId", -1).also {
            if (it > 0) {
                // getFromId 해서 얻기
                // detail 모드
            } else {
                // add 모드
                editTitle.requestFocus()
            }
        }
        model.getMemo(memoId)

        setSupportActionBar(toolbar)

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            crudMotionLayout.loadLayoutDescription(R.xml.scene_crud_landscape)
            imageRecycler.background = resources.getDrawable(R.drawable.border_left, null)
        } else {
            imageRecycler.background = resources.getDrawable(R.drawable.border_top, null)
        }

        val imageList = model.getImageList()
        imageList.observe(this, Observer {

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(model.getMenuId(), menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_gallery -> {
            val mimeTypes = arrayOf("image/jpeg", "image/png")
            model.checkPermissionAndRun(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                startActivityForResult(Intent().apply {
                    action = Intent.ACTION_PICK
                    type = MediaStore.Images.Media.CONTENT_TYPE
                    putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                }, REQUEST_CODE_GALLERY)
            }
            true
        }
        R.id.action_camera  -> {
            model.checkPermissionAndRun(
                    this,
                    arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                           )
                                       ) {
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).let { takePictureIntent ->
                    takePictureIntent.resolveActivity(packageManager)?.let { _ ->
                        val photoFile: File? = try {
                            model.createImageFile()
                        } catch (ex: IOException) {
                            null
                        }

                        photoFile?.let {
                            val uri = FileProvider.getUriForFile(
                                    this,
                                    "$packageName.fileprovider",
                                    it
                                                                )
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                            startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA)
                        }
                    }
                }
            }
            true
        }
        R.id.action_url     -> {
            true
        }
        R.id.action_save    -> {
            true
        }
        R.id.action_edit    -> {
            true
        }
        R.id.action_delete  -> {
            true
        }
        else                -> super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_GALLERY -> {
                    data?.data?.let { uri ->
//                        val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentUris.parseId(uri))
                        model.addPicture("$uri")
                    }
                }
                REQUEST_CODE_CAMERA  -> {

                }
            }
        }
    }
}
