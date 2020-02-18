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
    private val canDelete = MutableLiveData<Boolean>(true)
    private val imageList = MutableLiveData<MutableList<String>>(mutableListOf())
    val title = MutableLiveData<String>("")
    val body = MutableLiveData<String>("")

    fun getMemo(memoId: Int) {
        if (isInit) return
        isInit = true

        curMode = Mode.Detail
        curMenuId = R.menu.menu_detail
        canDelete.value = false
        memo = memoRepo.getMemoFromId(memoId)!!
        resetData()
    }

    fun resetData() {
        title.value = memo.title
        body.value = memo.text
        imageList.value?.clear()
        imageList.value?.addAll(memo.images)
    }

    fun saveData() {
        memo.title = title.value!!
        memo.text = body.value!!
        memo.images = imageList.value!!.toList()
        memoRepo.insertMemo(memo)
    }

    fun deleteMemo() {
        memoRepo.deleteMemo(memo)
    }

    fun getMenuId(): Int = curMenuId
    fun getImageList(): LiveData<MutableList<String>> = imageList
    fun getCanDelete(): LiveData<Boolean> = canDelete

    fun createImageFile(): File = pictureUtil.createImageFile()
    fun addPicture(uri: Uri) = imageList.add(uri.toString())
    fun addPicture(url: String) = imageList.add(url)
    fun removePicture(uri: String) = imageList.remove(uri)
    fun removePicture(idx: Int) = imageList.remove(idx)

    fun saveCameraImage() = pictureUtil.saveCameraImage()?.also { uri ->
        addPicture(uri)
    }

    fun changeMode(mode: Mode) = if (mode == Mode.Detail) {
        curMode = Mode.Detail
        curMenuId = R.menu.menu_detail
        canDelete.value = false
    } else {
        curMode = Mode.Edit
        curMenuId = R.menu.menu_add_and_edit
        canDelete.value = true
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
    private fun MutableLiveData<MutableList<String>>.add(item: String) {
        this.value?.add(item)
        this.value = this.value
    }

    private fun MutableLiveData<MutableList<String>>.remove(item: String) {
        this.value?.remove(item)
        this.value = this.value
    }

    private fun MutableLiveData<MutableList<String>>.remove(idx: Int) {
        this.value?.removeAt(idx)
        this.value = this.value
    }
}