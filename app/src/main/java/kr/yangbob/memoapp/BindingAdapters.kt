package kr.yangbob.memoapp

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

object BindingAdapters {
    @BindingAdapter("app:setUri")
    @JvmStatic
    fun setUri(view: ImageView, uri: String?) {
        uri?.let {
            Glide.with(view.context).load(it).into(view)
        }
    }

    @BindingAdapter("app:setTitle")
    @JvmStatic
    fun setTitle(view: TextView, title: String) {
        if (title.isBlank()) view.setText(R.string.main_no_title)
        else view.text = title
    }

    @BindingAdapter("app:setText")
    @JvmStatic
    fun setText(view: TextView, text: String) {
        if (text.isBlank()) view.setText(R.string.main_no_text)
        else view.text = text
    }

    @BindingAdapter("app:setThumbnail")
    @JvmStatic
    fun setThumbnail(view: ImageView, imageList: List<String>) {
        if (imageList.isEmpty()) view.visibility = View.GONE
        else {
            Glide.with(view.context).load(imageList[0]).override(200, 200).into(view)
        }
    }
}