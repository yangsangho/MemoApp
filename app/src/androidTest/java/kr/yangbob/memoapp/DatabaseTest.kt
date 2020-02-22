package kr.yangbob.memoapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kr.yangbob.memoapp.db.Memo
import kr.yangbob.memoapp.db.MemoDao
import kr.yangbob.memoapp.db.MemoDatabase
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
    private lateinit var memoDao: MemoDao
    private val db: MemoDatabase by inject()
    private val testId = 3
    private val testMemo = Memo("title", "body", mutableListOf("content://media/external/images/media/4172", "http://url.com"), testId)

    @Before
    fun createDb() {
        stopKoin()
        startKoin {
            androidContext(appContext)
            modules(listOf(viewModelModule, testDbModule))
        }
        memoDao = db.getMemoDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
        stopKoin()
    }

    @Test
    fun insertAndGetTest() {
        GlobalScope.launch {
            val nullMemo = memoDao.getFromId(testId)
            assertThat(nullMemo).isNull()


            val getMemo = memoDao.getFromId(testId)
            assertThat(getMemo).isEqualTo(testMemo)
        }
    }

    @Test
    fun deleteTest() {
        GlobalScope.launch {
            memoDao.insert(testMemo)

            val getMemo = memoDao.getFromId(testId)
            assertThat(getMemo).isNotNull()

            memoDao.delete(getMemo!!)
            val newGetMemo = memoDao.getFromId(testId)
            assertThat(newGetMemo).isNull()
        }
    }
}