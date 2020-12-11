package com.google.gradient.red.fragments

import android.app.Application
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import com.google.gradient.red.R
import com.google.gradient.red.data.models.Mood

class SharedViewModel(application: Application): AndroidViewModel(application) {

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
    private fun parseMood(mood: String): Mood {
        return when(mood) {
            "\uD83D\uDE03 Happy" -> {
                Mood.HAPPY}
            "\uD83D\uDE42 Okay" -> {
                Mood.OKAY}
            "\uD83D\uDE11 Upset" -> {
                Mood.UPSET}
            else -> {
                Mood.HAPPY}
        }
    }
}