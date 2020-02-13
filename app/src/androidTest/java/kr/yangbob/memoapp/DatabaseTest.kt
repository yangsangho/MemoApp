package kr.yangbob.memoapp

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
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseTest : KoinTest {
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    private val memoRepo: MemoRepo by inject()
    private val db: MemoDatabase by inject()
    private val testId = 3
    private val testMemo = Memo("title", "body", mutableListOf("1234", "http:://url.com"), testId)

    @Before
    fun createDb() {
        stopKoin()
        startKoin {
            androidContext(appContext)
            modules(listOf(viewModelModule, testDbModule))
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
        stopKoin()
    }

    @Test
    fun insertAndGetTest() {
        val nullMemo = memoRepo.getMemoFromId(testId)
        assertThat(nullMemo).isNull()

        memoRepo.insertMemo(testMemo)

        val memoList = memoRepo.getAllMemo()
        assertThat(memoList[0]).isEqualTo(testMemo)

        val getMemo = memoRepo.getMemoFromId(testId)
        assertThat(getMemo).isEqualTo(testMemo)
    }

    @Test
    fun deleteTest() {
        memoRepo.insertMemo(testMemo)

        val getMemo = memoRepo.getMemoFromId(testId)
        assertThat(getMemo).isNotNull()

        memoRepo.deleteMemo(getMemo!!)
        val newGetMemo = memoRepo.getMemoFromId(testId)
        assertThat(newGetMemo).isNull()
    }
}