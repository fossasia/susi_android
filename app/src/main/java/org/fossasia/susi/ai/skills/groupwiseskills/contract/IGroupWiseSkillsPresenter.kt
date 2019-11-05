package org.fossasia.susi.ai.skills.groupwiseskills.contract

/**
 *
 * Created by arundhati24 on 16/07/2018.
 */
interface IGroupWiseSkillsPresenter {

    fun onAttach(groupWiseSkillsView: IGroupWiseSkillsView)

    fun getSkills(swipeToRefreshActive: Boolean, group: String)

    fun onDetach()
}
