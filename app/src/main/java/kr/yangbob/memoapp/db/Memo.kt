package kr.yangbob.memoapp.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Memo(
    var title: String,
    var body: String,
    val pictures: MutableList<String>? = null,
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null
)