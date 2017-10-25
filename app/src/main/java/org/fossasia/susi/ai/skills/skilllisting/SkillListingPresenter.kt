package org.fossasia.susi.ai.skills.skilllisting

import org.fossasia.susi.ai.data.SkillListingModel
import org.fossasia.susi.ai.data.contract.ISkillListingModel
import org.fossasia.susi.ai.rest.responses.susi.ListGroupsResponse
import org.fossasia.susi.ai.rest.responses.susi.ListSkillsResponse
import org.fossasia.susi.ai.rest.responses.susi.SkillData
import org.fossasia.susi.ai.skills.skilllisting.contract.ISkillListingPresenter
import org.fossasia.susi.ai.skills.skilllisting.contract.ISkillListingView
import retrofit2.Response

/**
 * Skill Listing Presenter
 *
 * Created by chiragw15 on 15/8/17.
 */
class SkillListingPresenter: ISkillListingPresenter,
        ISkillListingModel.onFetchGroupsFinishedListener, ISkillListingModel.onFetchSkillsFinishedListener {

    var skillListingModel: ISkillListingModel = SkillListingModel()
    var skillListingView: ISkillListingView ?= null
    var count = 0
    var skills: ArrayList<Pair<String, Map<String, SkillData>>> = ArrayList()
    var groupsCount = 0
    var groups:  List<String> = ArrayList()

    override fun onAttach(skillListingView: ISkillListingView) {
        this.skillListingView = skillListingView
    }

    override fun getGroups() {
        skillListingView?.visibilityProgressBar(true)
        skillListingModel.fetchGroups(this)
    }

    override fun onGroupFetchSuccess(response: Response<ListGroupsResponse>) {
        if (response.isSuccessful && response.body() != null) {
            groupsCount = response.body().groups.size
            groups = response.body().groups
            skillListingModel.fetchSkills(groups[0], this)
        } else {
            skillListingView?.visibilityProgressBar(false)
            skillListingView?.displayError()
        }
    }

    override fun onGroupFetchFailure(t: Throwable) {
        skillListingView?.visibilityProgressBar(false)
        skillListingView?.displayError()
    }

    override fun onSkillFetchSuccess(response: Response<ListSkillsResponse>, group: String) {
        skillListingView?.visibilityProgressBar(false)
        if (response.isSuccessful && response.body() != null) {
            val responseSkillMap = response.body().skillMap
            if(responseSkillMap.isNotEmpty()) {
                skills.add(Pair(group, responseSkillMap))
                skillListingView?.updateAdapter(skills)
            }
            count++
            if(count != groupsCount) {
                skillListingModel.fetchSkills(groups[count], this)
            }
        } else {
            skillListingView?.visibilityProgressBar(false)
            skillListingView?.displayError()
        }
    }

    override fun onSkillFetchFailure(t: Throwable) {
        skillListingView?.visibilityProgressBar(false)
        skillListingView?.displayError()
    }

    override fun onDetach() {
        skillListingModel.cancelFetch()
        skillListingView = null
    }
}
