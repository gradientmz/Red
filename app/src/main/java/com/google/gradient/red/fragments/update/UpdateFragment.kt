package com.google.gradient.red.fragments.update

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gradient.red.R
import com.google.gradient.red.data.models.JournalData
import com.google.gradient.red.data.viewmodel.JournalViewModel
import com.google.gradient.red.fragments.SharedViewModel
import kotlinx.android.synthetic.main.fragment_update.*
import kotlinx.android.synthetic.main.fragment_update.view.*

class updateFragment : Fragment() {

    // Variables used later
    private val mSharedViewModel: SharedViewModel by viewModels()
    private val mJournalViewModel: JournalViewModel by viewModels()
    private val args by navArgs<updateFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_update, container, false)

        // set menu
        setHasOptionsMenu(true)

        view.current_title_et.setText(args.currentItem.title)
        view.current_description_et.setText(args.currentItem.description)
        view.current_mood_spinner.setSelection(mSharedViewModel.parseMood(args.currentItem.mood))
        view.current_mood_spinner.onItemSelectedListener = mSharedViewModel.listener

        return view
    }

    // Creates check mark at the top of the fragment
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_fragment_menu, menu)
    }

    // Controls buttons in menu, calls update and delete functions when needed
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_save -> updateItem()
            R.id.menu_delete -> confirmItemRemoval()
        }
        return super.onOptionsItemSelected(item)
    }

    // Called when updating save entry (called when check clicked)
    private fun updateItem() {
        val title = current_title_et.text.toString()
        val description = current_description_et.text.toString()
        val getMood = current_mood_spinner.selectedItem.toString()
        val validation = mSharedViewModel.verifyDataFromUser(title, description)
        val editedDate = args.currentItem.date

        // Updates current item
        if (validation) {
            val updatedItem = JournalData(
                args.currentItem.id,
                title,
                mSharedViewModel.parseMood(getMood),
                description,
                editedDate
            )
            mJournalViewModel.updateData(updatedItem)
            Toast.makeText(requireContext(), "Entry successfully edited!", Toast.LENGTH_SHORT).show()

            // Navigates back to home/list fragment once finished
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
            } else {
                Toast.makeText(requireContext(), "You have some empty fields.", Toast.LENGTH_SHORT).show()
            }
    }

    // Opens an alert dialog asking if they want deletion, deletes if positive
    private fun confirmItemRemoval() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            mJournalViewModel.deleteItem(args.currentItem)
            Toast.makeText(
                requireContext(),
                "Your entry was successfully removed!",
                Toast.LENGTH_SHORT
            ).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Entry deletion")
        builder.setMessage("Are you sure you want to delete this journal entry?")
        builder.create().show()
    }
}