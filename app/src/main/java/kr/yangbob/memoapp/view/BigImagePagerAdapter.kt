package kr.yangbob.memoapp.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kr.yangbob.memoapp.R
import kr.yangbob.memoapp.databinding.PagerItemBigImageBinding

class BigImagePagerAdapter(private var imageList: List<String>, private val activity: BigImageActivity) : RecyclerView.Adapter<BigImageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BigImageViewHolder {
        val binding: PagerItemBigImageBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.pager_item_big_image, parent, false)
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
}

class BigImageViewHolder(private val binding: PagerItemBigImageBinding) : RecyclerView.ViewHolder(binding.root) {
    fun onBind(uri: String) {
        binding.uri = uri
    }
}