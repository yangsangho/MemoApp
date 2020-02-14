package kr.yangbob.memoapp.view

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class ImageListAdapter(private var imageList: List<String>) : RecyclerView.Adapter<ImageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int = imageList.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun updateList(newImageList: List<String>){
        val diffCallback = ImageListDiffCallback(imageList, newImageList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        imageList = newImageList
        diffResult.dispatchUpdatesTo(this)
    }
}

class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

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