package com.google.gradient.red.fragments.list

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gradient.red.R
import com.google.gradient.red.data.viewmodel.JournalViewModel
import com.google.gradient.red.fragments.SharedViewModel
import kotlinx.android.synthetic.main.fragment_list.view.*

class ListFragment : Fragment() {

    private val mJournalViewModel: JournalViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()

    private val adapter: ListAdapter by lazy { ListAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        val recyclerView = view.recyclerView

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())

        // Checks if database is empty whenever database changes
        mJournalViewModel.getAllData.observe(viewLifecycleOwner, { data ->
            mSharedViewModel.checkIfDatabaseEmpty(data)
            adapter.setData(data)
        })

        // Calls showEmptyDatabaseViews function
        mSharedViewModel.emptyDatabase.observe(viewLifecycleOwner, Observer {
            showEmptyDatabaseViews(it)
        })

        // Takes user to new entry fragment when clicked
        view.floatingActionButton.setOnClickListener  {
            findNavController().navigate(R.id.action_listFragment_to_addFragment)
        }

        // set menu
        setHasOptionsMenu(true)

        return view
    }

    // Function to make no_data views visible if database is empty, else invisible
    private fun showEmptyDatabaseViews(emptyDatabase: Boolean) {
        if (emptyDatabase) {
            view?.no_data_imageview?.visibility = View.VISIBLE
            view?.no_data_textview?.visibility = View.VISIBLE
        } else {
            view?.no_data_imageview?.visibility = View.INVISIBLE
            view?.no_data_textview?.visibility = View.INVISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_fragment_menu, menu)
    }
}