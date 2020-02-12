package kr.yangbob.memoapp.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MemoDao {
    @Query("SELECT * FROM Memo")
    suspend fun getAll(): List<Memo>

    @Query("SELECT * FROM MEMO WHERE id = :id")
    suspend fun getFromId(id: Int): Memo

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(memo: Memo)
}