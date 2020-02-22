package kr.yangbob.memoapp.view

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kr.yangbob.memoapp.R
import kr.yangbob.memoapp.databinding.ListItemMemoBinding
import kr.yangbob.memoapp.db.Memo
import kr.yangbob.memoapp.db.checkEqual

class MemoListAdapter : RecyclerView.Adapter<MemoViewHolder>() {
    private var memoList: List<Memo> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val binding = DataBindingUtil.inflate<ListItemMemoBinding>(LayoutInflater.from(parent.context), R.layout.list_item_memo, parent, false)
        return MemoViewHolder(binding)
    }

    override fun getItemCount(): Int = memoList.size

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        holder.bind(memoList[position])
    }

    fun updateList(newMemoList: List<Memo>) {
        val diffResult = MemoListDiffCallback(memoList, newMemoList).let {
            DiffUtil.calculateDiff(it)
        }
        memoList = newMemoList
        diffResult.dispatchUpdatesTo(this)
    }
}

class MemoViewHolder(private val binding: ListItemMemoBinding) : RecyclerView.ViewHolder(binding.root) {
    private lateinit var memo: Memo

    init {
        binding.holder = this
    }

    fun bind(memo: Memo) {
        binding.memo = memo
        this.memo = memo
    }

    fun clickMemo(view: View) {
        view.context.startActivity(Intent(view.context, CrudActivity::class.java).apply {
            putExtra("memoId", memo.id)
        })
    }
}

class MemoListDiffCallback(
        private val oldMemoList: List<Memo>,
        private val newMemoList: List<Memo>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldMemoList.size
    override fun getNewListSize(): Int = newMemoList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldMemoList[oldItemPosition].id == newMemoList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldMemo = oldMemoList[oldItemPosition]
        val newMemo = newMemoList[newItemPosition]
        return oldMemo.checkEqual(newMemo)
    }
}