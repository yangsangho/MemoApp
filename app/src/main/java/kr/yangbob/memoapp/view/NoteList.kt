package kr.yangbob.memoapp.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.yangbob.memoapp.R

class NoteListAdapter(private val list: List<String>) : RecyclerView.Adapter<NoteViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder =
            NoteViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                            R.layout.list_item_note,
                            parent,
                            false
                    )
            )

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(list[position])
    }
}

class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(str: String) {

    }
}