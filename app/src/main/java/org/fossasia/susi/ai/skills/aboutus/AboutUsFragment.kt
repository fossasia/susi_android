package org.fossasia.susi.ai.skills.aboutus

import android.os.Build
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v4.app.Fragment
import android.text.Html
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_about_us.about_us_scrollview
import kotlinx.android.synthetic.main.fragment_about_us.know_more_about_susi
import kotlinx.android.synthetic.main.fragment_about_us.about_susi
import kotlinx.android.synthetic.main.fragment_about_us.contributors_desc
import kotlinx.android.synthetic.main.fragment_about_us.susi_report_issues_desc
import kotlinx.android.synthetic.main.fragment_about_us.susi_license_info_desc
import kotlinx.android.synthetic.main.fragment_about_us.susi_skill_cms_desc
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.skills.SkillsActivity
import org.fossasia.susi.ai.skills.settings.ChatSettingsFragment

class AboutUsFragment : Fragment() {

    var downX = 0.0f
    var upX = 0.0f
    private val TAG_SETTINGS_FRAGMENT = "SettingsFragment"

    @NonNull
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val thisActivity = activity
        if (thisActivity is SkillsActivity) thisActivity.title = getString(R.string.action_about_us)
        val rootView = inflater.inflate(R.layout.fragment_about_us, container, false)
        setHasOptionsMenu(true)

        return rootView
    }

    private fun handleAboutUsGesture() {
        about_us_scrollview.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        downX = event.getX()
                    }
                    MotionEvent.ACTION_UP -> {
                        upX = event?.getX()

                        var deltaX = downX - upX
                        if (deltaX < -900) { //Handling swipe to left only
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

    @NonNull
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        about_susi.text = htmlConverter(R.string.susi_about)
        contributors_desc.text = htmlConverter(R.string.susi_contributors_desc)
        susi_skill_cms_desc.text = htmlConverter(R.string.susi_skill_cms_desc)
        susi_license_info_desc.text = htmlConverter(R.string.susi_license_information_desc)
        susi_report_issues_desc.text = htmlConverter(R.string.susi_report_issues_desc)
        know_more_about_susi.text = htmlConverter(R.string.susi_know_more)

        about_susi.setMovementMethod(LinkMovementMethod.getInstance())
        contributors_desc.setMovementMethod(LinkMovementMethod.getInstance())
        susi_skill_cms_desc.setMovementMethod(LinkMovementMethod.getInstance())
        susi_license_info_desc.setMovementMethod(LinkMovementMethod.getInstance())
        susi_report_issues_desc.setMovementMethod(LinkMovementMethod.getInstance())
        know_more_about_susi.setMovementMethod(LinkMovementMethod.getInstance())
        handleAboutUsGesture()
    }

    private fun htmlConverter(resourceId: Int): Spanned {
        return when {
            Build.VERSION.SDK_INT > 24 -> Html.fromHtml(getString(resourceId), Html.FROM_HTML_OPTION_USE_CSS_COLORS)
            else -> @Suppress("DEPRECATION") Html.fromHtml(getString(resourceId))
        }
    }
}