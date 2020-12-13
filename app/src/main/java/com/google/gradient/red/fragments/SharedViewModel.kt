package com.google.gradient.red.fragments

import android.app.Application
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gradient.red.R
import com.google.gradient.red.data.models.JournalData
import com.google.gradient.red.data.models.Mood

//  This file handles changing mood picker text color
class SharedViewModel(application: Application): AndroidViewModel(application) {

    // Checks if journal database is empty
    val emptyDatabase: MutableLiveData<Boolean> = MutableLiveData(true)

    fun checkIfDatabaseEmpty(journalData: List<JournalData>) {
        emptyDatabase.value = journalData.isEmpty()
    }

    // Listens for when mood selector changes
    val listener: AdapterView.OnItemSelectedListener = object :
        AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(p0: AdapterView<*>?) {}

        // Checks which option the spinner is on and sets text color
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            when(position) {
                0 -> { (parent?.getChildAt(0) as TextView).setTextColor(ContextCompat.getColor(application, R.color.pgreen)) }
                1 -> { (parent?.getChildAt(0) as TextView).setTextColor(ContextCompat.getColor(application, R.color.pblue)) }
                2 -> { (parent?.getChildAt(0) as TextView).setTextColor(ContextCompat.getColor(application, R.color.pred)) }
            }
        }

    }

    // Gets title and description as parameters and checks if either is empty
    fun verifyDataFromUser(title: String, description: String): Boolean {
        return if(TextUtils.isEmpty(title) || TextUtils.isEmpty(description)) {
            false
        } else !(title.isEmpty() || description.isEmpty())
    }

    // Reads the mood spinner and assigns a mood value.
    fun parseMood(mood: String): Mood {
        return when(mood) {
            "Happy" -> {Mood.HAPPY}
            "Okay" -> {Mood.OKAY}
            "Upset" -> {Mood.UPSET}
            else -> {Mood.HAPPY}
        }
    }

    // Parses mood, and returns integer value of mood selected
    fun parseMood(mood: Mood): Int {
        return when(mood) {
            Mood.HAPPY -> 0
            Mood.OKAY -> 1
            Mood.UPSET -> 2
        }
    }
}