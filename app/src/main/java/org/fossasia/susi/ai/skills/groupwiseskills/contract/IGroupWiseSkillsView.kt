package org.fossasia.susi.ai.skills.groupwiseskills.contract

import org.fossasia.susi.ai.dataclasses.GroupWiseSkills

/**
 *
 * Created by arundhati24 on 16/07/2018.
 */
interface IGroupWiseSkillsView {

    fun visibilityProgressBar(boolean: Boolean)

    fun updateAdapter(skills: GroupWiseSkills)

    fun showEmptySkillsListMessage()

    fun displayError()
}
