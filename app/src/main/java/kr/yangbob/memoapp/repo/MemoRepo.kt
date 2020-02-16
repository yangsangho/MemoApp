package kr.yangbob.memoapp.repo

import androidx.lifecycle.LiveData
import kotlinx.coroutines.runBlocking
import kr.yangbob.memoapp.db.Memo
import kr.yangbob.memoapp.db.MemoDao
import kr.yangbob.memoapp.db.MemoDatabase

class MemoRepo(memoDB: MemoDatabase) {
    private val memoDao: MemoDao = memoDB.getMemoDao()

    fun getAllMemoLD(): LiveData<List<Memo>> = memoDao.getAllLD()
    fun getAllMemo(): List<Memo> = runBlocking { memoDao.getAll() }
    fun getMemoFromId(id: Int): Memo? = runBlocking { memoDao.getFromId(id) }
    fun insertMemo(memo: Memo) = runBlocking { memoDao.insert(memo) }
    fun deleteMemo(memo: Memo) = runBlocking { memoDao.delete(memo) }
}