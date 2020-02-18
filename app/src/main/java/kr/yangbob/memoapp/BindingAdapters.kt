package kr.yangbob.memoapp

import android.graphics.drawable.Drawable
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kr.yangbob.memoapp.view.CrudActivity

object BindingAdapters {
    @BindingAdapter("app:setUriBig")
    @JvmStatic
    fun setUriBig(view: ImageView, uri: String?) {
        uri?.let {
            Glide.with(view.context).load(uri)
                    .error(R.drawable.ic_img_load_error)
                    .into(view)
        }
    }

    @BindingAdapter("app:setUriSmall")
    @JvmStatic
    fun setUriSmall(view: ImageView, uri: String?) {
        uri?.let {
            val crudActivity = view.context as CrudActivity

            Glide.with(crudActivity).load(uri)
                    .error(R.drawable.ic_img_load_error)
//                    .placeholder(R.drawable.ic_img_loading)
                    .override(200, 200)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            Toast.makeText(crudActivity, R.string.no_photo_msg, Toast.LENGTH_LONG).show()
                            crudActivity.runOnUiThread {
                                Handler().postDelayed({
                                    crudActivity.removePicture(uri)
                                }, 1000)
                            }
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            return false
                        }
                    }).into(view)
        }
    }

    @BindingAdapter("app:setTitle")
    @JvmStatic
    fun setTitle(view: TextView, title: String) {
        if (title.isBlank()) view.setText(R.string.main_no_title)
        else view.text = title
    }

    @BindingAdapter("app:setBody")
    @JvmStatic
    fun setBody(view: TextView, text: String) {
        if (text.isBlank()) view.setText(R.string.main_no_body)
        else view.text = text
    }

    @BindingAdapter("app:setThumbnail")
    @JvmStatic
    fun setThumbnail(view: ImageView, imageList: List<String>) {
        if (imageList.isEmpty()) view.visibility = View.GONE
        else {
            if (view.visibility == View.GONE) view.visibility = View.VISIBLE
            Glide.with(view.context).load(imageList.first())
                    .error(R.drawable.ic_img_load_error)
                    .placeholder(R.drawable.ic_img_loading)
                    .override(300, 300).into(view)
        }
    }
}