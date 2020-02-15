package kr.yangbob.memoapp.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Memo(
        var title: String = "",
        var text: String = "",
        var images: List<String> = listOf(),
        @PrimaryKey(autoGenerate = true)
        val id: Int? = null
)

fun Memo.isNull(): Boolean = title.isBlank() || text.isBlank() || images.isEmpty()

fun Memo.checkEqual(chkTitle: String, chkText: String, chkPictures: List<String>): Boolean =
        (title == chkTitle) && (text == chkText) && (images == chkPictures)
