package com.google.gradient.red.fragments.list

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gradient.red.R
import com.google.gradient.red.data.models.JournalData
import com.google.gradient.red.data.viewmodel.JournalViewModel
import com.google.gradient.red.fragments.SharedViewModel
import com.google.gradient.red.fragments.list.adapter.SwipeToDelete
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator
import kotlinx.android.synthetic.main.fragment_list.view.*

class ListFragment : Fragment(), SearchView.OnQueryTextListener {

    private val mJournalViewModel: JournalViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()

    private val adapter: ListAdapter by lazy { ListAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        // Set up recycler view
        val recyclerView = view.recyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.itemAnimator = SlideInLeftAnimator().apply {
            addDuration = 300
        }

        // Set up swipe to delete
        swipeToDelete(recyclerView)

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

    // Sets up menu + search bar
    private fun restoreDeletedData(view: View, deletedItem: JournalData, position: Int) {
        val snackBar = Snackbar.make(
            view,
            "Deleted journal entry!",
            Snackbar.LENGTH_LONG
        )
        snackBar.setAction("Undo") {
            mJournalViewModel.insertData(deletedItem)
            adapter.notifyItemChanged(position)
        }
        snackBar.show()
    }

    // Handles list fragment menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_fragment_menu, menu)

        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
    }

    // Handles when menu items are clicked
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sort_goodmood -> { mJournalViewModel.sortByGoodMood.observe(this, Observer { adapter.setData(it) }) }
            R.id.sort_badmood -> { mJournalViewModel.sortByBadMood.observe(this, Observer { adapter.setData(it) }) }
        }
        return super.onOptionsItemSelected(item)
    }

    // Function to handle swipe to delete
    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeToDeleteCallback = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem = adapter.dataList[viewHolder.adapterPosition]
                mJournalViewModel.deleteItem(deletedItem)
                restoreDeletedData(viewHolder.itemView, deletedItem, viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    // Check journal when character typed or changed
    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchThroughDatabase(query)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query != null) {
            searchThroughDatabase(query)
        }
        return true
    }

    // Searches through journal database
    private fun searchThroughDatabase(query: String) {
        var searchQuery = query
        searchQuery = "%$searchQuery%"

        mJournalViewModel.searchDatabase(searchQuery).observe(this, Observer { list ->
            list?.let {
                adapter.setData(it)
            }
        })
    }
}