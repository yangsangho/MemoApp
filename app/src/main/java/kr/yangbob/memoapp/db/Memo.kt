package kr.yangbob.memoapp.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Memo(
        var title: String? = null,
        var body: String? = null,
        val pictures: MutableList<String>? = null,
        @PrimaryKey(autoGenerate = true)
        val id: Int? = null
               )