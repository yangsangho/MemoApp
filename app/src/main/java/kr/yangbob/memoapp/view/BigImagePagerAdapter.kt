package kr.yangbob.memoapp.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import kr.yangbob.memoapp.R
import kr.yangbob.memoapp.databinding.PagerItemBigImageBinding

class BigImagePagerAdapter(private val imageList: Array<String>) : RecyclerView.Adapter<BigImageViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BigImageViewHolder {
        val binding: PagerItemBigImageBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.pager_item_big_image, parent, false)
        return BigImageViewHolder(binding)
    }

    override fun getItemCount(): Int = imageList.size

    override fun onBindViewHolder(holder: BigImageViewHolder, position: Int) {
        holder.onBind(imageList[position])
    }
}

class BigImageViewHolder(private val binding: PagerItemBigImageBinding) : RecyclerView.ViewHolder(binding.root){
    fun onBind(uri: String){
        binding.uri = uri
    }
}