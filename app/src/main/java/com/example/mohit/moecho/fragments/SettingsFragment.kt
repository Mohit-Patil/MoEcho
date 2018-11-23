package com.example.mohit.moecho.fragments


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.CompoundButton
import android.widget.Switch
import com.example.mohit.moecho.R

class SettingsFragment : Fragment() {
    var myActivity: Activity? = null
    var shakeSwitch: Switch? = null

    object Statified {
        var MY_PREFS_NAME = "ShakeFeature"
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        activity?.title = "Settings"
        shakeSwitch = view?.findViewById(R.id.switchButton)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val prefs = myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)
        val isAllowed = prefs?.getBoolean("feature", false)
        if (isAllowed as Boolean) {
            shakeSwitch?.isChecked = true
        } else {
            shakeSwitch?.isChecked = false
        }
        shakeSwitch?.setOnCheckedChangeListener({ compoundButton, b ->
            if (b) {
                val editor = myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)?.edit()
                editor?.putBoolean("feature", true)
                editor?.apply()
            } else {
                val editor = myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)?.edit()
                editor?.putBoolean("feature", false)
                editor?.apply()
            }

        })
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item = menu?.findItem(R.id.action_sort)
        item?.isVisible = false
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.menu_blank, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

}
