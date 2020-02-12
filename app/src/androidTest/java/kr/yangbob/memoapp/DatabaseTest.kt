package kr.yangbob.memoapp

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import kr.yangbob.memoapp.db.Memo
import kr.yangbob.memoapp.db.MemoDatabase
import kr.yangbob.memoapp.repo.MemoRepo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var memoRepo: MemoRepo
    private lateinit var db: MemoDatabase

    @Before
    fun createDb() {
        db = Room.inMemoryDatabaseBuilder(
            appContext,
            MemoDatabase::class.java
        ).build()
        memoRepo = MemoRepo(db)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetTest(){
        val memo = Memo("title", "body", mutableListOf("1234", "http:://url.com"),3)
        memoRepo.insertMemo(memo)
        val memoList = memoRepo.getAllMemo()
        assertThat(memoList[0]).isEqualTo(memo)
        val getMemo = memoRepo.getMemoFromId(3)
        assertThat(getMemo).isEqualTo(memo)
    }
}