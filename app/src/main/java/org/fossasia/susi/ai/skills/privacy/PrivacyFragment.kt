package org.fossasia.susi.ai.skills.privacy

import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.helper.BaseFragment

class PrivacyFragment : BaseFragment() {
    override fun getTitle(): String {
        return getString(R.string.settings_privacy) // To change body of created functions use File | Settings | File Templates.
    }

    override val rootLayout: Int
        get() = R.layout.fragment_privacy // To change initializer of created properties use File | Settings | File Templates.
}
