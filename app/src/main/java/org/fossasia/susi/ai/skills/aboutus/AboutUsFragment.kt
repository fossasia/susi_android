package org.fossasia.susi.ai.skills.aboutus

import android.os.Build
import android.os.Bundle
import android.support.annotation.NonNull
import android.text.Html
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.View
import kotlinx.android.synthetic.main.fragment_about_us.*
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.helper.BaseFragment

class AboutUsFragment : BaseFragment() {

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
    }

    private fun htmlConverter(resourceId: Int): Spanned {
        return when {
            Build.VERSION.SDK_INT > 24 -> Html.fromHtml(getString(resourceId), Html.FROM_HTML_OPTION_USE_CSS_COLORS)
            else -> @Suppress("DEPRECATION") Html.fromHtml(getString(resourceId))
        }
    }

    override fun getTitle(): String {
        return getString(R.string.action_about_us)
    }

    override val rootLayout: Int
        get() = R.layout.fragment_about_us // To change initializer of created properties use File | Settings | File Templates.
}
