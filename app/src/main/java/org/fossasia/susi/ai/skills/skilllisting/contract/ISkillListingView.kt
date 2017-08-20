package org.fossasia.susi.ai.skills.skilllisting.contract

import org.fossasia.susi.ai.rest.responses.susi.SkillData

/**
 *
 * Created by chiragw15 on 15/8/17.
 */
interface ISkillListingView {
    fun visibilityProgressBar(boolean: Boolean)
    fun updateAdapter(skills: ArrayList<Pair<String, Map<String, SkillData>>>)
    fun displayErrorDialog()
}