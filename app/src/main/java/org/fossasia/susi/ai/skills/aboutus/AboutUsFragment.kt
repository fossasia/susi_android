package org.fossasia.susi.ai.skills.aboutus


import android.net.Uri
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.customtabs.CustomTabsIntent
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_about_us.*
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
        about_susi.setOnClickListener({
            try {
                var uri = Uri.parse(getString(R.string.url_about_susi))
                var builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()  //Custom tabs intent builder
                var customTabsIntent = builder.build()
                customTabsIntent.launchUrl(context, uri) //launching through custom tabs
            } catch (e: Exception) {
                Toast.makeText(context, getString(R.string.link_unavailable), Toast.LENGTH_SHORT).show()
            }
        })

        contributors_desc.setOnClickListener({
            try {
                var uri = Uri.parse(getString(R.string.url_susi_contributors))
                var builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()  //Custom tabs intent builder
                var customTabsIntent = builder.build()
                customTabsIntent.launchUrl(context, uri) //launching through custom tabs
            } catch (e: Exception) {
                Toast.makeText(context, getString(R.string.link_unavailable), Toast.LENGTH_SHORT).show()
            }
        })

        susi_skill_cms_desc.setOnClickListener({
            try {
                var uri = Uri.parse(getString(R.string.url_susi_skill_cms))
                var builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder() //custom tabs intent builder
                var customTabsIntent = builder.build()
                customTabsIntent.launchUrl(context, uri)  //launching through custom tabs
            } catch (e: Exception) {
                Toast.makeText(context, getString(R.string.link_unavailable), Toast.LENGTH_SHORT).show()
            }
        })

        susi_report_issues_desc.setOnClickListener({
            try {
                var uri = Uri.parse(getString(R.string.url_susi_report_issue))
                var builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder() //custom tabs intent builder
                var customTabsIntent = builder.build()
                customTabsIntent.launchUrl(context, uri)  //launching through custom tabs
            } catch (e: Exception) {
                Toast.makeText(context, getString(R.string.link_unavailable), Toast.LENGTH_SHORT).show()
            }
        })
        susi_license_info_desc.setOnClickListener({
            try {
                var uri = Uri.parse(getString(R.string.url_susi_license))
                var builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder() //custom tabs intent builder
                var customTabsIntent = builder.build()
                customTabsIntent.launchUrl(context, uri) //launching through custom tabs
            } catch (e: Exception) {
                Toast.makeText(context, getString(R.string.link_unavailable), Toast.LENGTH_SHORT).show()
            }
        })
        super.onViewCreated(view, savedInstanceState)
    }

}
