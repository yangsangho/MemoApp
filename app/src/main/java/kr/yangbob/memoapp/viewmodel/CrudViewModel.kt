package kr.yangbob.memoapp.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tedpark.tedpermission.rx2.TedRx2Permission
import kr.yangbob.memoapp.R
import kr.yangbob.memoapp.db.Memo
import kr.yangbob.memoapp.repo.MemoRepo
import kr.yangbob.memoapp.repo.PictureUtil
import java.io.File

class CrudViewModel(private val memoRepo: MemoRepo, private val pictureUtil: PictureUtil) :
        ViewModel() {
    private var chooseMenuId = R.menu.menu_add_and_edit
    private lateinit var memo: Memo

    val title = MutableLiveData<String>()
    val body = MutableLiveData<String>()
    private val imageList = MutableLiveData<MutableList<String>>()

    fun getMemo(memoId: Int) {
        memo = if (memoId == -1) {
            Memo()
        } else {
            memoRepo.getMemoFromId(memoId)!!
        }

        imageList.value = mutableListOf()
        memo.pictures?.also {
            imageList.value?.addAll(it)
        }
    }

    fun getMenuId(): Int = chooseMenuId
    fun createImageFile(): File = pictureUtil.createImageFile()
    fun getImageList(): LiveData<MutableList<String>> = imageList
    fun addPicture(uri: Uri) {
        imageList.value?.add(uri.toString())
    }

    fun addPicture(url: String) {
        imageList.value?.add(url)
    }

    @SuppressLint("CheckResult")
    fun checkPermissionAndRun(
            context: Context,
            permissionList: Array<String>,
            run: () -> Unit
                             ) {
        if (!TedRx2Permission.isGranted(context, *permissionList)) {
            val deniedList =
                TedRx2Permission.getDeniedPermissions(context, *permissionList).toTypedArray()

            var cntGranted = 0
            var cntDenied = 0
            TedRx2Permission.with(context)
                    .setRationaleTitle("title")
                    .setRationaleMessage("message") // "we need permission for read contact and find your location"
                    .setPermissions(*deniedList)
                    .request()
                    .subscribe(
                            { tedPermissionResult ->
                                if (tedPermissionResult.isGranted) {
                                    cntGranted++
                                    if (cntGranted == deniedList.size) run()
                                } else {
                                    cntDenied++
                                    if (cntGranted == (cntGranted + cntDenied)) {
                                        Toast.makeText(
                                                context,
                                                "Permission Denied\n" + tedPermissionResult.deniedPermissions.toString(),
                                                Toast.LENGTH_SHORT
                                                      ).show()
                                    }

                                }
                            },
                            { }
                              )
        } else {
            run()
        }
    }
}