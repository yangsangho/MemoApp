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
    private lateinit var memo: Memo
    private val canDelete = MutableLiveData<Boolean>()
    private val imageList = MutableLiveData<MutableList<String>>(mutableListOf())
    private val _isNoItem = MutableLiveData<Boolean>()

    var lastActionIsAdd = false
    val isNoItem: LiveData<Boolean> = _isNoItem
    val title = MutableLiveData<String>()
    val body = MutableLiveData<String>()

    fun getCanDelete(): LiveData<Boolean> = canDelete
    fun getMemo(memoId: Int) {
        if (isInit) return
        isInit = true

        if (memoId > 0) {
            curMode = Mode.Detail
            curMenuId = R.menu.menu_detail
            canDelete.value = false
            memo = memoRepo.getMemoFromId(memoId)!!
            resetData()
        } else {
            memo = Memo()
            canDelete.value = true
            _isNoItem.value = true
            title.value = ""
            body.value = ""
        }
    }

    fun resetData() {
        title.value = memo.title
        body.value = memo.body
        imageList.value?.clear()
        _isNoItem.value = memo.images.isEmpty()
        imageList.addAll(memo.images)
    }

    fun saveData() {
        memo.title = title.value!!
        memo.body = body.value!!
        memo.images = imageList.value!!.toList()
        memoRepo.insertMemo(memo)
    }

    fun deleteMemo() {
        memoRepo.deleteMemo(memo)
    }

    fun getMenuId(): Int = curMenuId
    fun getImageList(): LiveData<MutableList<String>> = imageList

    fun createImageFile(): File = pictureUtil.createImageFile()
    fun addPicture(uri: Uri) = imageList.add(uri.toString())
    fun addPicture(url: String) = imageList.add(url)
    fun modifyUrl(idx: Int, url: String) = imageList.replace(idx, url)
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
        lastActionIsAdd = true
        if (_isNoItem.value!!) _isNoItem.value = false
        this.value?.add(item)
        this.value = this.value
    }

    private fun MutableLiveData<MutableList<String>>.addAll(list: List<String>) {
        this.value?.addAll(list)
        this.value = this.value
    }

    private fun MutableLiveData<MutableList<String>>.remove(item: String) {
        lastActionIsAdd = false
        this.value?.remove(item)
        if (this.value!!.isEmpty()) {
            _isNoItem.value = true
        }
        this.value = this.value
    }

    private fun MutableLiveData<MutableList<String>>.remove(idx: Int) {
        lastActionIsAdd = false
        this.value?.removeAt(idx)
        if (this.value!!.isEmpty()) {
            _isNoItem.value = true
        }
        this.value = this.value
    }

    private fun MutableLiveData<MutableList<String>>.replace(idx: Int, url: String) {
        this.value?.also {
            if (it[idx] != url) {
                it[idx] = url
                this.value = this.value
            }
        }
    }
}