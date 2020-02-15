package kr.yangbob.memoapp

import android.widget.ImageView
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
}