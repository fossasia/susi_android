package org.fossasia.susi.ai.skills.skillactivity.contract

import org.fossasia.susi.ai.skills.skilllisting.contract.ISkillListingView

interface ISkillActivityPresenter {
  //At the start of Activity
    fun onAttach(skillActivityView: ISkillActivityView)
    fun handleMenuSearch()
    fun isSearchOpened(): Boolean
}
