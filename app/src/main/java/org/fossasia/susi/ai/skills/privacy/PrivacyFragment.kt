package org.fossasia.susi.ai.skills.privacy

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.fossasia.susi.ai.R

class PrivacyFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val thisActivity = activity
        thisActivity?.title = getString(R.string.settings_privacy)
        val rootView = inflater.inflate(R.layout.fragment_privacy, container, false)
        setHasOptionsMenu(true)
        return rootView
    }
}
