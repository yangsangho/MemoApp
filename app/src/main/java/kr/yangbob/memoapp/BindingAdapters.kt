package kr.yangbob.memoapp

import android.view.View
import android.webkit.URLUtil
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import kr.yangbob.memoapp.view.MainActivity

object BindingAdapters {
//    .listener(object : RequestListener<Drawable> {
//        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
//            Toast.makeText(crudActivity, R.string.no_photo_msg, Toast.LENGTH_LONG).show()
//            crudActivity.runOnUiThread {
//                Handler().postDelayed({
//                    crudActivity.removePicture(uri)
//                }, 1000)
//            }
//            return false
//        }
//
//        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
//            return false
//        }
//    })

    @BindingAdapter("app:setUriBig", "app:setRequestManager")
    @JvmStatic
    fun setUriBig(view: ImageView, uri: String?, requestManager: RequestManager) {
        uri?.let {
            val requestBuilder = requestManager.load(uri).error(R.drawable.ic_img_load_error)
            if (!URLUtil.isContentUrl(uri)) requestBuilder.placeholder(R.drawable.ic_img_loading).thumbnail(0.3f)
            requestBuilder.into(view)
        }
    }

    @BindingAdapter("app:setUriSmall", "app:setRequestManager")
    @JvmStatic
    fun setUriSmall(view: ImageView, uri: String?, requestManager: RequestManager) {
        uri?.let {
            val requestBuilder = requestManager.load(uri).error(R.drawable.ic_img_load_error).override(200, 200)
            if (!URLUtil.isContentUrl(uri)) requestBuilder.placeholder(R.drawable.ic_img_loading).thumbnail(0.3f)
            requestBuilder.into(view)
        }
    }

    @BindingAdapter("app:setThumbnail")
    @JvmStatic
    fun setThumbnail(view: ImageView, imageList: List<String>) {
        if (imageList.isEmpty()) view.visibility = View.GONE
        else {
            if (view.visibility == View.GONE) view.visibility = View.VISIBLE
            val uri = imageList.first()
            val mainActivity = view.context as MainActivity
            val requestBuilder = Glide.with(mainActivity).load(uri).error(R.drawable.ic_img_load_error).override(300, 300)
            if (!URLUtil.isContentUrl(uri)) requestBuilder.placeholder(R.drawable.ic_img_loading).thumbnail(0.3f)
            requestBuilder.into(view)
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
}