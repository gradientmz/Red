package com.google.gradient.red.fragments.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.gradient.red.R
import com.google.gradient.red.data.models.JournalData
import com.google.gradient.red.data.models.Mood
import kotlinx.android.synthetic.main.row_layout.view.*

class ListAdapter: RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    var dataList = emptyList<JournalData>()

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_layout, parent, false))
    }

    // Gets number of items in db
    override fun getItemCount(): Int {
        return dataList.size
    }

    // Gets values from views and writes them in room db
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.title_txt.text = dataList[position].title
        holder.itemView.description_txt.text = dataList[position].description

        // Sets color of mood indicator card (dot) based on saved mood
        val mood = dataList[position].mood
        when(mood) {
            Mood.HAPPY -> holder.itemView.mood_indicator.setCardBackgroundColor(ContextCompat.getColor(
                holder.itemView.context,
                R.color.pgreen))
            Mood.OKAY -> holder.itemView.mood_indicator.setCardBackgroundColor(ContextCompat.getColor(
                holder.itemView.context,
                R.color.pblue))
            Mood.UPSET -> holder.itemView.mood_indicator.setCardBackgroundColor(ContextCompat.getColor(
                holder.itemView.context,
                R.color.pred))
        }
    }

    fun setData(journalData: List<JournalData>) {
        this.dataList = journalData
        notifyDataSetChanged()
    }
}