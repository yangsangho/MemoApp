package kr.yangbob.memoapp.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kr.yangbob.memoapp.R
import kr.yangbob.memoapp.databinding.ListItemImageBinding

class ImageListAdapter : RecyclerView.Adapter<ImageViewHolder>() {
    private var imageList: List<String> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = DataBindingUtil.inflate<ListItemImageBinding>(
                LayoutInflater.from(parent.context),
                R.layout.list_item_image,
                parent,
                false
        )
        return ImageViewHolder(binding)
    }

    override fun getItemCount(): Int = imageList.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.onBind(imageList[position])
    }

    fun updateList(newImageList: List<String>) {
        val diffCallback = ImageListDiffCallback(imageList, newImageList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        imageList = newImageList
        diffResult.dispatchUpdatesTo(this)
    }
}

class ImageViewHolder(private val binding: ListItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
    fun onBind(uri: String) {
        binding.uri = uri
    }
}

class ImageListDiffCallback(
        private val oldImageList: List<String>,
        private val newImageList: List<String>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldImageList.size
    override fun getNewListSize(): Int = newImageList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldImageList[oldItemPosition] == newImageList[newItemPosition]

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldImageList[oldItemPosition] == newImageList[newItemPosition]

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}