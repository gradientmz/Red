package com.google.gradient.red.fragments.list.adapter

import androidx.recyclerview.widget.DiffUtil
import com.google.gradient.red.data.models.JournalData

// This file is to
class JournalDiffUtil (
    private val oldList: List<JournalData>,
    private val newList: List<JournalData>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] === newList[newItemPosition]
    }

    // Checks if all contents + id are the same, returns boolean
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
                && oldList[oldItemPosition].title == newList[newItemPosition].title
                && oldList[oldItemPosition].description == newList[newItemPosition].description
                && oldList[oldItemPosition].mood == newList[newItemPosition].mood
    }

}