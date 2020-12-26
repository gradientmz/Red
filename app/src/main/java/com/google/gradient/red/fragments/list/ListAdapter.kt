package com.google.gradient.red.fragments.list

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.gradient.red.R
import com.google.gradient.red.data.models.JournalData
import com.google.gradient.red.data.models.Mood
import com.google.gradient.red.fragments.list.adapter.JournalDiffUtil
import kotlinx.android.synthetic.main.row_layout.view.*

class ListAdapter: RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    var dataList = emptyList<JournalData>()

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_layout,
                parent,
                false
            )
        )
    }

    // Gets number of items in db
    override fun getItemCount(): Int {
        return dataList.size
    }

    // Does various things, including writing room changes and determining correct entry
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.title_txt.text = dataList[position].title
        holder.itemView.description_txt.text = dataList[position].description
        holder.itemView.row_background.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToUpdateFragment(dataList[position])
            holder.itemView.findNavController().navigate(action)
            Log.d(
                "Entry Clicked",
                "A journal entry was clicked, taking the user to the update fragment."
            )
        }
        holder.itemView.date.text = dataList[position].date

        // Checks if image exists
        if (dataList[position].image.width > 0) {
            holder.itemView.image.setImageBitmap(dataList[position].image)
        }

        val palette = Palette.Builder(dataList[position].image).generate()
        val dominantSwatch = palette.dominantSwatch
        val color = dominantSwatch!!.rgb

        holder.itemView.imagecard.setCardBackgroundColor(color)

        // Sets color of mood indicator card (dot) based on saved mood
        val mood = dataList[position].mood
        when(mood) {
            Mood.HAPPY -> holder.itemView.mood_indicator.setCardBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.pgreen
                )
            )
            Mood.OKAY -> holder.itemView.mood_indicator.setCardBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.pblue
                )
            )
            Mood.UPSET -> holder.itemView.mood_indicator.setCardBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.pred
                )
            )
        }
    }

    fun setData(journalData: List<JournalData>) {
        val journalDiffUtil = JournalDiffUtil(dataList, journalData)
        val journalDiffResult = DiffUtil.calculateDiff(journalDiffUtil)
        this.dataList = journalData
        journalDiffResult.dispatchUpdatesTo(this)
    }
}