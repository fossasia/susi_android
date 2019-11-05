package org.fossasia.susi.ai.skills.skillSearch

import org.fossasia.susi.ai.dataclasses.GroupWiseSkills
import org.fossasia.susi.ai.skills.groupwiseskills.contract.IGroupWiseSkillsPresenter
import org.fossasia.susi.ai.skills.groupwiseskills.contract.IGroupWiseSkillsView

/**
 *
 * Created by naman653 on 28/03/2019.
 */
class SearchSkillPresenter(val searchSkillFragment: SearchSkillFragment, val skills: GroupWiseSkills) : IGroupWiseSkillsPresenter {
    private var groupWiseSkillsView: IGroupWiseSkillsView? = null

    override fun onAttach(groupWiseSkillsView: IGroupWiseSkillsView) {
        this.groupWiseSkillsView = groupWiseSkillsView
    }

    override fun getSkills(swipeToRefreshActive: Boolean, group: String) {
        groupWiseSkillsView?.visibilityProgressBar(!swipeToRefreshActive)
        groupWiseSkillsView?.updateAdapter(skills)
    }

    override fun onDetach() {
        groupWiseSkillsView = null
    }
}
