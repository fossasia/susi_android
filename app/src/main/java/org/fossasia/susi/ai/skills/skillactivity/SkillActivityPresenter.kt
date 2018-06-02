package org.fossasia.susi.ai.skills.skillactivity

import org.fossasia.susi.ai.skills.skillactivity.contract.ISkillActivityPresenter
import org.fossasia.susi.ai.skills.skillactivity.contract.ISkillActivityView

class SkillActivityPresenter (skillsActivity: SkillsActivity): ISkillActivityPresenter{

    var isSearchedOpen = false
    private var skillActivityView: ISkillActivityView? = null

    override fun onAttach(skillActivityView: ISkillActivityView) {
        this.skillActivityView = skillActivityView
    }

    override fun handleMenuSearch() {
        if(isSearchedOpen)
        {
            skillActivityView?.closeSearch()
            skillActivityView?.hideKeyboard()
            isSearchedOpen = false;
        }else{
            skillActivityView?.openSearch()
            isSearchedOpen = true;
        }
    }

    override fun isSearchOpened(): Boolean {
        return isSearchedOpen
    }
}