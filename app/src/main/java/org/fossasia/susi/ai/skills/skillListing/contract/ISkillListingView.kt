package org.fossasia.susi.ai.skills.skillListing.contract

import org.fossasia.susi.ai.rest.responses.susi.SkillData

/**
 *
 * Created by chiragw15 on 15/8/17.
 */
interface ISkillListingView {
    fun visibilityProgressBar(boolean: Boolean)
    fun setAdapter(skills: MutableList<Pair<String, Map<String, SkillData>>>)
}