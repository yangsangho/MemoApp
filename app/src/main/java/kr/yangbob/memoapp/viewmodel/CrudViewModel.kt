package kr.yangbob.memoapp.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.yangbob.memoapp.Mode
import kr.yangbob.memoapp.R
import kr.yangbob.memoapp.db.Memo
import kr.yangbob.memoapp.db.checkEqual
import kr.yangbob.memoapp.repo.MemoRepo
import kr.yangbob.memoapp.repo.PictureUtil
import java.io.File

class CrudViewModel(private val memoRepo: MemoRepo, private val pictureUtil: PictureUtil) :
        ViewModel() {
    private var isInit = false
    private var curMode = Mode.Add
    private var curMenuId = R.menu.menu_add_and_edit
    private var memo: Memo = Memo()
    val title = MutableLiveData<String>()
    val body = MutableLiveData<String>()
    private val imageList = MutableLiveData<List<String>>()

    init {
        resetData()
    }

    fun getMemo(memoId: Int) {
        if (isInit) return
        isInit = true

        curMode = Mode.Detail
        curMenuId = R.menu.menu_detail
        memo = memoRepo.getMemoFromId(memoId)!!
        resetData()
    }

    fun resetData() {
        title.value = memo.title
        body.value = memo.text
        imageList.value = memo.images
    }

    fun saveData() {
        memo.title = title.value!!
        memo.text = body.value!!
        memo.images = imageList.value!!
        memoRepo.insertMemo(memo)
    }

    fun deleteMemo() {
        memoRepo.deleteMemo(memo)
    }

    fun getMenuId(): Int = curMenuId
    fun getImageList(): LiveData<List<String>> = imageList

    fun createImageFile(): File = pictureUtil.createImageFile()
    fun addPicture(uri: Uri) {
        imageList.value = imageList.value!! + listOf(uri.toString())
    }

    fun addPicture(url: String) {
        imageList.value = imageList.value!! + listOf(url)
    }

    fun removePicture(uri: String) {
        imageList.value = imageList.value!! - listOf(uri)
    }

    fun saveCameraImage() {
        pictureUtil.saveCameraImage()?.let { uri ->
            addPicture(uri)
        }
    }

    fun changeMode(mode: Mode) = if (mode == Mode.Detail) {
        curMode = Mode.Detail
        curMenuId = R.menu.menu_detail
    } else {
        curMode = Mode.Edit
        curMenuId = R.menu.menu_add_and_edit
    }

    fun isAddMode(): Boolean = curMode == Mode.Add
    fun isDetailMode(): Boolean = curMode == Mode.Detail
    fun isEditMode(): Boolean = curMode == Mode.Edit
    fun hasChange(): Boolean = if (curMode == Mode.Add) {
        !chkNullInputData()
    } else {
        !memo.checkEqual(title.value!!, body.value!!, imageList.value!!)
    }

    private fun chkNullInputData(): Boolean = title.value!!.isBlank() && body.value!!.isBlank() && imageList.value!!.isEmpty()
}