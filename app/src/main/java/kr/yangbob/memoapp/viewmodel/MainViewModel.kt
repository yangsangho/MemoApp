package kr.yangbob.memoapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.yangbob.memoapp.db.Memo
import kr.yangbob.memoapp.repo.MemoRepo

class MainViewModel(private val memoRepo: MemoRepo) : ViewModel() {

    private val memoList: LiveData<List<Memo>> = memoRepo.getAllMemoLD()
    private val _isNoItem = MutableLiveData<Boolean>(false)
    val isNoItem: LiveData<Boolean> = _isNoItem

    fun getMemoList() = memoList
    fun setIsNoItem(value: Boolean) {
        if(value != _isNoItem.value) _isNoItem.value = value
    }
}