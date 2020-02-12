package kr.yangbob.memoapp

import androidx.room.Room
import kr.yangbob.memoapp.db.MemoDatabase
import kr.yangbob.memoapp.repo.MemoRepo
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val viewModelModule = module {
    single { MemoRepo(get()) }
}
val dbModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            MemoDatabase::class.java, "MemoDB"
        ).fallbackToDestructiveMigration().build()
    }
}
val testDbModule = module {
    single {
        Room.inMemoryDatabaseBuilder(
            androidContext(),
            MemoDatabase::class.java
        ).build()
    }
}