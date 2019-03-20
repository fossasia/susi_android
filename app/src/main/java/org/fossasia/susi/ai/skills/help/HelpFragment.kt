package org.fossasia.susi.ai.skills.help

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.fossasia.susi.ai.R

class HelpFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val thisActivity = activity
        thisActivity?.title = getString(R.string.action_help)
        val rootView = inflater.inflate(R.layout.fragment_help, container, false)
        setHasOptionsMenu(true)
        return rootView
    }
}
