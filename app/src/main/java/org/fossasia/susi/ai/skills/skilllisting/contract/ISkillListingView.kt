package org.fossasia.susi.ai.skills.skilllisting.contract

import org.fossasia.susi.ai.dataclasses.SkillsBasedOnMetrics
import org.fossasia.susi.ai.rest.responses.susi.SkillData

/**
 *
 * Created by chiragw15 on 15/8/17.
 */
interface ISkillListingView {
    fun visibilityProgressBar(boolean: Boolean)
    fun updateAdapter(metrics: SkillsBasedOnMetrics)
    fun displayError()
    fun updateSkillsAdapter(skills: ArrayList<Pair<String, List<SkillData>>>)
}
