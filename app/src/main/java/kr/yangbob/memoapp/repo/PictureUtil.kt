package kr.yangbob.memoapp.repo

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class PictureUtil(context: Context) {
    private var currentPhotoPath: String? = null
    private val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    private val contentResolver: ContentResolver = context.contentResolver

    @Throws(IOException::class)
    fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String =
                SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA).format(Date())
        return File.createTempFile(
                "IMG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    fun insert(file: File) {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }
        val uri: Uri =
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
        contentResolver.openFileDescriptor(uri, "w", null)?.use { pfd ->
            FileOutputStream(pfd.fileDescriptor).use { fileOutput ->
                BufferedOutputStream(fileOutput).use { bufferedOutput ->
                    FileInputStream(file).use { fileInput ->
                        BufferedInputStream(fileInput).use { bufferedInput ->
                            val byteArray = ByteArray(1024)
                            while (bufferedInput.read(byteArray) != -1) {
                                bufferedOutput.write(byteArray)
                            }
                        }
                    }
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            contentResolver.update(uri, values, null, null)
        }
    }
}