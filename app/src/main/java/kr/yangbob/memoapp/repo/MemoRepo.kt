package kr.yangbob.memoapp.repo

import androidx.lifecycle.LiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kr.yangbob.memoapp.db.Memo
import kr.yangbob.memoapp.db.MemoDao
import kr.yangbob.memoapp.db.MemoDatabase

class MemoRepo(memoDB: MemoDatabase) {
    private val memoDao: MemoDao = memoDB.getMemoDao()
    private val memoList = memoDao.getAll()

    fun getAllMemo(): LiveData<List<Memo>> = memoList
    fun getMemoFromId(id: Int): Memo? = runBlocking { memoDao.getFromId(id) }
    fun insertMemo(memo: Memo) = GlobalScope.launch { memoDao.insert(memo) }
    fun deleteMemo(memo: Memo) = GlobalScope.launch { memoDao.delete(memo) }
}