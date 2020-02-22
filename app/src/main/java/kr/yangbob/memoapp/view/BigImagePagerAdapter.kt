package kr.yangbob.memoapp.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import kr.yangbob.memoapp.R
import kr.yangbob.memoapp.databinding.PagerItemBigImageBinding

class BigImagePagerAdapter(private var imageList: List<String>, private val activity: BigImageActivity) : RecyclerView.Adapter<BigImageViewHolder>() {
    private val glideRequestManager = Glide.with(activity)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BigImageViewHolder {
        val binding: PagerItemBigImageBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.pager_item_big_image, parent, false)
        binding.requestManager = glideRequestManager
        binding.bigImage.setOnClickListener {
            activity.toggleSystemUi()
        }
        return BigImageViewHolder(binding)
    }

    override fun getItemCount(): Int = imageList.size

    override fun onBindViewHolder(holder: BigImageViewHolder, position: Int) {
        holder.onBind(imageList[position])
    }

    fun updateList(newImageList: List<String>) {
        val diffResult = ImageListDiffCallback(imageList, newImageList).let {
            DiffUtil.calculateDiff(it)
        }
        imageList = newImageList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onViewRecycled(holder: BigImageViewHolder) {
        holder.clear(glideRequestManager)
        super.onViewRecycled(holder)
    }
}

class BigImageViewHolder(private val binding: PagerItemBigImageBinding) : RecyclerView.ViewHolder(binding.root) {
    fun onBind(uri: String) {
        binding.uri = uri
    }

    fun clear(requestManager: RequestManager) {
        requestManager.clear(binding.bigImage)
    }
}