package kr.yangbob.memoapp.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MemoDao {
    @Query("SELECT * FROM Memo")
    fun getAll(): LiveData<List<Memo>>

    @Query("SELECT * FROM MEMO WHERE id = :id")
    suspend fun getFromId(id: Int): Memo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(memo: Memo)

    @Delete
    suspend fun delete(memo: Memo)
}