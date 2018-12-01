package com.example.mohit.moecho.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import com.example.mohit.moecho.R

class AboutUsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = "About Me"
        return inflater.inflate(R.layout.fragment_about_us, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item: MenuItem? = menu?.findItem(R.id.action_sort)
        val searchItem = menu?.findItem(R.id.search)
        item?.isVisible = false
        searchItem?.isVisible = false
    }


}