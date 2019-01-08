package org.fossasia.susi.ai.skills.aboutus


import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.customtabs.CustomTabsIntent
import android.support.v4.app.Fragment
import android.text.Html
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_about_us.about_susi
import kotlinx.android.synthetic.main.fragment_about_us.contributors_desc
import kotlinx.android.synthetic.main.fragment_about_us.susi_skill_cms_desc
import kotlinx.android.synthetic.main.fragment_about_us.susi_report_issues_desc
import kotlinx.android.synthetic.main.fragment_about_us.susi_license_info_desc
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.skills.SkillsActivity


class AboutUsFragment : Fragment() {

    @NonNull
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as SkillsActivity).title = (activity as SkillsActivity).getString(R.string.action_about_us)
        val rootView = inflater.inflate(R.layout.fragment_about_us, container, false)
        setHasOptionsMenu(true)
        return rootView
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val itemAbout = menu.findItem(R.id.menu_about)
        itemAbout.isVisible = false
        val itemSettings = menu.findItem(R.id.menu_settings)
        itemSettings.isVisible = false
        val searchoption = menu.findItem(R.id.action_search)
        searchoption.isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    @NonNull
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if (Build.VERSION.SDK_INT >= 24) {
            about_susi.text = Html.fromHtml(getString(R.string.susi_about), Html.FROM_HTML_OPTION_USE_CSS_COLORS) // for 24 api and more
        } else {
            about_susi.text = Html.fromHtml(getString(R.string.susi_about)) // or for older api
        }

        if (Build.VERSION.SDK_INT >= 24) {
            contributors_desc.text = Html.fromHtml(getString(R.string.susi_contributors_desc), Html.FROM_HTML_OPTION_USE_CSS_COLORS) // for 24 api and more
        } else {
            contributors_desc.text = Html.fromHtml(getString(R.string.susi_contributors_desc)) // or for older api
        }

        if (Build.VERSION.SDK_INT >= 24) {
            susi_skill_cms_desc.text = Html.fromHtml(getString(R.string.susi_skill_cms_desc), Html.FROM_HTML_OPTION_USE_CSS_COLORS) // for 24 api and more
        } else {
            susi_skill_cms_desc.text = Html.fromHtml(getString(R.string.susi_skill_cms_desc)) // or for older api
        }

        if (Build.VERSION.SDK_INT >= 24) {
            susi_license_info_desc.text = Html.fromHtml(getString(R.string.susi_license_information_desc), Html.FROM_HTML_OPTION_USE_CSS_COLORS) // for 24 api and more
        } else {
            susi_license_info_desc.text = Html.fromHtml(getString(R.string.susi_license_information_desc)) // or for older api
        }

        if (Build.VERSION.SDK_INT >= 24) {
            susi_report_issues_desc.text = Html.fromHtml(getString(R.string.susi_report_issues_desc), Html.FROM_HTML_OPTION_USE_CSS_COLORS) // for 24 api and more
        } else {
            susi_report_issues_desc.text = Html.fromHtml(getString(R.string.susi_report_issues_desc)) // or for older api
        }


        about_susi.setOnClickListener{
            val uri = Uri.parse(getString(R.string.url_about_susi))
            launchCustomtTab(uri)
        }

        contributors_desc.setOnClickListener{
            val uri = Uri.parse(getString(R.string.url_susi_contributors))
            launchCustomtTab(uri)
        }

        susi_skill_cms_desc.setOnClickListener{
            val uri = Uri.parse(getString(R.string.url_susi_skill_cms))
            launchCustomtTab(uri)
        }

        susi_report_issues_desc.setOnClickListener{
            val uri = Uri.parse(getString(R.string.url_susi_report_issue))
            launchCustomtTab(uri)
        }
        susi_license_info_desc.setOnClickListener{
            val uri = Uri.parse(getString(R.string.url_susi_license))
            launchCustomtTab(uri)
        }
        super.onViewCreated(view, savedInstanceState)
    }

    fun launchCustomtTab(uri: Uri) {
        try {
            CustomTabsIntent.Builder().build().launchUrl(context, uri) //launching through custom tabs
        } catch (e: Exception) {
            Toast.makeText(context, getString(R.string.link_unavailable), Toast.LENGTH_SHORT).show()
        }
    }
}
