package org.fossasia.susi.ai.skills.help

import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.helper.BaseFragment

class HelpFragment : BaseFragment() {
    override fun getTitle(): String {
        return getString(R.string.action_help)
    }

    override val rootLayout: Int
        get() = R.layout.fragment_help
}
