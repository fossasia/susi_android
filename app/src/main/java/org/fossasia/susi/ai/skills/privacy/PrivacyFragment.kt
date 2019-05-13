package org.fossasia.susi.ai.skills.privacy

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_privacy.privacy_scrollview
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.skills.settings.ChatSettingsFragment

class PrivacyFragment : Fragment() {

    var downX = 0.0f
    var upX = 0.0f
    private val TAG_SETTINGS_FRAGMENT = "SettingsFragment"

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handlePrivacyGestures()
    }

    private fun handlePrivacyGestures() {
        privacy_scrollview.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        downX = event.getX()
                    }
                    MotionEvent.ACTION_UP -> {
                        upX = event?.getX()

                        var deltaX = downX - upX

                        if (deltaX < -900) { //Hnadling swipe to left only
                            fragmentManager?.beginTransaction()
                                    ?.replace(R.id.fragment_container, ChatSettingsFragment(), TAG_SETTINGS_FRAGMENT)
                                    ?.addToBackStack(TAG_SETTINGS_FRAGMENT)
                                    ?.commit()
                        }
                    }
                }
                return false
            }
        }
        )
    }
}
