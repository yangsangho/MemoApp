package kr.yangbob.memoapp.db

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import android.webkit.URLUtil
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
    fun listToString(list: MutableList<String>): String {
        val mapList =
                list.map { if (URLUtil.isContentUrl(it)) ContentUris.parseId(Uri.parse(it)).toString() else it }
        return Gson().toJson(mapList, pictureListType)
    }

    @TypeConverter
    fun stringToList(str: String): MutableList<String> {
        val list: MutableList<String> = Gson().fromJson(str, pictureListType)
        return list.map {
            if (URLUtil.isValidUrl(it)) it
            else ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, it.toLong())
                    .toString()
        }.toMutableList()
    }
}