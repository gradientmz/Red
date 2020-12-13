package com.google.gradient.red.fragments.update

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.gradient.red.R
import com.google.gradient.red.fragments.SharedViewModel
import kotlinx.android.synthetic.main.fragment_update.view.*

class updateFragment : Fragment() {

    private val mSharedViewModel: SharedViewModel by viewModels()
//    private val args by navArgs<UpdateFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_update, container, false)

        // set menu
        setHasOptionsMenu(true)

//        view.current_title_et.setText(args.currentItem.title)
//        view.current_description_et.setText(args.currentItem.description)
        view.current_mood_spinner.setSelection(0)


        view.current_mood_spinner.onItemSelectedListener = mSharedViewModel.listener

        return view
    }

    // Creates check mark at the top of the fragment
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_fragment_menu, menu)
    }
}