package com.wil8dev.keep.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.wil8dev.keep.R
import com.wil8dev.keep.model.Note
import kotlinx.android.synthetic.main.item_note.view.*

class NoteAdapter(
    private val noteList: List<Note>,
    private val itemClickListener: View.OnClickListener
) :
    RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewItem =
            LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return ViewHolder(viewItem)
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = noteList[position]
        holder.itemView.cardView.setOnClickListener(itemClickListener)
        holder.itemView.cardView.tag = position

        if (note.title.isNotEmpty()) {
            holder.itemView.title.text = note.title
            holder.itemView.title.visibility = View.VISIBLE
        } else {
            holder.itemView.title.visibility = View.GONE
        }
        if (note.content.isNotEmpty()) {
            holder.itemView.content.text = note.content
            holder.itemView.content.visibility = View.VISIBLE
        } else {
            holder.itemView.content.visibility = View.GONE
            val params = RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 0)
            holder.itemView.title.layoutParams = params
        }
    }
}