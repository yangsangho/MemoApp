package kr.yangbob.memoapp.view

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import kr.yangbob.memoapp.R
import kr.yangbob.memoapp.databinding.ListItemImageBinding

class ImageListAdapter(private val activity: CrudActivity) : RecyclerView.Adapter<ImageViewHolder>() {
    private var imageList: List<String> = listOf()
    private val glideRequestManager = Glide.with(activity)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = DataBindingUtil.inflate<ListItemImageBinding>(
                LayoutInflater.from(parent.context),
                R.layout.list_item_image,
                parent,
                false
        )
        binding.requestManager = glideRequestManager
        binding.lifecycleOwner = activity
        binding.activity = activity
        return ImageViewHolder(binding, activity)
    }

    override fun getItemCount(): Int = imageList.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.onBind(imageList[position])
    }

    fun updateList(newImageList: List<String>) {
        val diffResult = ImageListDiffCallback(imageList, newImageList).let {
            DiffUtil.calculateDiff(it)
        }
        imageList = newImageList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onViewRecycled(holder: ImageViewHolder) {
        holder.clear(glideRequestManager)
        super.onViewRecycled(holder)
    }
}

class ImageViewHolder(private val binding: ListItemImageBinding, private val activity: CrudActivity) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.holder = this
    }

    fun onBind(uri: String) {
        binding.uri = uri
    }

    fun clear(requestManager: RequestManager) {
        requestManager.clear(binding.image)
    }

    fun clickImageDeleteBtn(view: View) {
        activity.removePicture(adapterPosition)
    }

    fun clickImage(view: View) {
        activity.startBigImage(adapterPosition)
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
}