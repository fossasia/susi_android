package org.fossasia.susi.ai.skills.skilllisting.contract

/**
 *
 * Created by chiragw15 on 15/8/17.
 */
interface ISkillListingPresenter {
    fun onAttach(skillListingView: ISkillListingView)
    fun getGroups()
    fun onDetach()
}