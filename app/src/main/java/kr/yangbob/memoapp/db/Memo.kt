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
        (title == chkTitle) && (text == chkText) && (images.deepEquals(chkPictures))

fun Memo.checkEqual(other: Memo): Boolean =
        (title == other.title) && (text == other.text) && (images.deepEquals(other.images))

fun List<String>.deepEquals(other: List<String>): Boolean {
    if (this.size == other.size) return this.mapIndexed { idx, str -> str == other[idx] }.all { it }
    return false
}