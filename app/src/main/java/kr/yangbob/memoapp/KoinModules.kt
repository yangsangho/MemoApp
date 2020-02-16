package kr.yangbob.memoapp

import androidx.room.Room
import kr.yangbob.memoapp.db.MemoDatabase
import kr.yangbob.memoapp.repo.MemoRepo
import kr.yangbob.memoapp.repo.PictureUtil
import kr.yangbob.memoapp.viewmodel.CrudViewModel
import kr.yangbob.memoapp.viewmodel.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MainViewModel(get()) }
    viewModel { CrudViewModel(get(), get()) }
    single { PictureUtil(androidContext()) }
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