package org.fossasia.susi.ai.skills.aboutus

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.customtabs.CustomTabsIntent
import android.support.v4.app.Fragment
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_about_us.about_susi
import kotlinx.android.synthetic.main.fragment_about_us.contributors_desc
import kotlinx.android.synthetic.main.fragment_about_us.susi_skill_cms_desc
import kotlinx.android.synthetic.main.fragment_about_us.susi_report_issues_desc
import kotlinx.android.synthetic.main.fragment_about_us.susi_license_info_desc
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.skills.SkillsActivity

class AboutUsFragment : Fragment() {

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

    @NonNull
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //aboutUs textview got spanned
        val aboutUsString = SpannableString(about_susi.text)
        val clickableSpan=object : ClickableSpan(){
            override fun onClick(textView: View) {
                val uri = Uri.parse(getString(R.string.url_about_susi))
                   launchCustomtTab(uri)
            }
        }
        aboutUsString.setSpan(clickableSpan, 0, 7 , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        about_susi.text = aboutUsString
        about_susi.movementMethod = LinkMovementMethod.getInstance()

        //Contributors
        val contributorsString = SpannableString(contributors_desc.text)
        val contributorsSpan=object : ClickableSpan(){
            override fun onClick(textView: View) {
                val uri = Uri.parse(getString(R.string.url_susi_contributors))
                launchCustomtTab(uri)
            }
        }
        contributorsString.setSpan(contributorsSpan, 13, 25 , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        contributors_desc.text = contributorsString
        contributors_desc.movementMethod = LinkMovementMethod.getInstance()

        //SUSI_SKILLS_CMS_DESC
        val skillsString = SpannableString(susi_skill_cms_desc.text)
        val skillsSpan=object : ClickableSpan(){
            override fun onClick(textView: View) {
                val uri = Uri.parse(getString(R.string.url_susi_skill_cms))
                launchCustomtTab(uri)
            }
        }
        skillsString.setSpan(skillsSpan, 71, 86 , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        susi_skill_cms_desc.text = skillsString
        susi_skill_cms_desc.movementMethod = LinkMovementMethod.getInstance()

        //SUSI license info description
        val licenseString = SpannableString(susi_license_info_desc.text)
        val licenseSpan=object : ClickableSpan(){
            override fun onClick(textView: View) {
                val uri = Uri.parse(getString(R.string.url_susi_license))
                launchCustomtTab(uri)
            }
        }
        licenseString.setSpan(licenseSpan, 83, 94 , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        susi_license_info_desc.text = licenseString
        susi_license_info_desc.movementMethod = LinkMovementMethod.getInstance()


        //SUSI report info description
        val reportString = SpannableString(susi_report_issues_desc.text)
        val reportSpan=object : ClickableSpan(){
            override fun onClick(textView: View) {
                val uri = Uri.parse(getString(R.string.url_susi_report_issue))
                launchCustomtTab(uri)
            }
        }
        reportString.setSpan(reportSpan, 32, 63 , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        susi_report_issues_desc.text = reportString
        susi_report_issues_desc.movementMethod = LinkMovementMethod.getInstance()


        super.onViewCreated(view, savedInstanceState)
    }

    fun launchCustomtTab(uri: Uri) {
        try {
            CustomTabsIntent.Builder().build().launchUrl(context, uri) //launching through custom tabs
        } catch (e: Exception) {
            Toast.makeText(context, getString(R.string.link_unavailable), Toast.LENGTH_SHORT).show()
        }
    }

    private fun htmlConverter(resourceId: Int): Spanned {
        return when {
            Build.VERSION.SDK_INT>24 -> Html.fromHtml(getString(resourceId), Html.FROM_HTML_OPTION_USE_CSS_COLORS)
            else -> @Suppress("DEPRECATION") Html.fromHtml(getString(resourceId))
        }
    }
}
