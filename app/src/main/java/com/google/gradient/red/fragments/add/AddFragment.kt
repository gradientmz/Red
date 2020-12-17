package com.google.gradient.red.fragments.add

import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.gradient.red.R
import com.google.gradient.red.data.models.JournalData
import com.google.gradient.red.data.models.Mood
import com.google.gradient.red.data.viewmodel.JournalViewModel
import com.google.gradient.red.fragments.SharedViewModel
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.fragment_add.view.*
import java.text.SimpleDateFormat
import java.util.*

class addFragment : Fragment() {

    private val mJournalViewModel: JournalViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()
    var currentDate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        // set menu
        setHasOptionsMenu(true)

        view.mood_spinner.onItemSelectedListener = mSharedViewModel.listener

        // Set date and time for currentDate
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm a")
        currentDate = sdf.format(Date())

        return view
    }

    // Creates check mark at the top of the fragment
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_fragment_menu, menu)
    }

    // If the check mark is clicked, entry gets added
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_add) {
            insertDataToDb()
        }
        return super.onOptionsItemSelected(item)
    }

    // uses below function to check if text is empty, and gets values from add fragment
    private fun insertDataToDb() {
        val mTitle = title_et.text.toString()
        val mMood = mood_spinner.selectedItem.toString()
        val mDescription = description_et.text.toString()
        val mDate = currentDate.toString()

        val validation = mSharedViewModel.verifyDataFromUser(mTitle, mDescription)
        if(validation) {
            val newData = JournalData(
                0,
                mTitle,
                parseMood(mMood),
                mDescription,
                mDate
            )
            mJournalViewModel.insertData(newData)
            Toast.makeText(requireContext(), "New entry added!", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_addFragment_to_listFragment) // Jetpack Navigation
        } else {
            Toast.makeText(requireContext(), "You have some empty fields.", Toast.LENGTH_SHORT).show()
        }
    }

    // Gets title and description as parameters and checks if either is empty
    private fun verifyDataFromUser(title: String, description: String): Boolean {
        return if(TextUtils.isEmpty(title) || TextUtils.isEmpty(description)) {
            false
        } else !(title.isEmpty() || description.isEmpty())
    }

    // Reads the mood spinner and assigns a mood value.
    private fun parseMood(mood: String): Mood {
        return when(mood) {
            "Happy" -> {Mood.HAPPY}
            "Okay" -> {Mood.OKAY}
            "Upset" -> {Mood.UPSET}
            else -> {Mood.HAPPY}
        }
    }
}