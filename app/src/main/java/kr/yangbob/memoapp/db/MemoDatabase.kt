package kr.yangbob.memoapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Database(entities = [Memo::class], version = 1)
@TypeConverters(Converters::class)
abstract class MemoDatabase : RoomDatabase() {
    abstract fun getMemoDao(): MemoDao
}

class Converters {
    private val pictureListType = object : TypeToken<MutableList<String>>() {}.type

    @TypeConverter
    fun listToString(list: MutableList<String>): String = Gson().toJson(list, pictureListType)

    @TypeConverter
    fun stringToList(str: String): MutableList<String> = Gson().fromJson(str, pictureListType)
}