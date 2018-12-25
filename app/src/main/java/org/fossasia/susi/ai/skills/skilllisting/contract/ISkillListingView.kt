package org.fossasia.susi.ai.skills.skilllisting.contract

import org.fossasia.susi.ai.dataclasses.SkillsBasedOnMetrics

/**
 *
 * Created by chiragw15 on 15/8/17.
 */
interface ISkillListingView {
    fun visibilityProgressBar(boolean: Boolean)
    fun updateAdapter(metrics: SkillsBasedOnMetrics)
    fun displayError()
}