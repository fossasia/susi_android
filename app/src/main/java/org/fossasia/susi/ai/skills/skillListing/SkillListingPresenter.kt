package org.fossasia.susi.ai.skills.skillListing

import org.fossasia.susi.ai.data.SkillListingModel
import org.fossasia.susi.ai.data.UtilModel
import org.fossasia.susi.ai.data.contract.ISkillListingModel
import org.fossasia.susi.ai.data.contract.IUtilModel
import org.fossasia.susi.ai.rest.responses.susi.ListGroupsResponse
import org.fossasia.susi.ai.rest.responses.susi.ListSkillsResponse
import org.fossasia.susi.ai.rest.responses.susi.SkillData
import org.fossasia.susi.ai.skills.SkillsActivity
import org.fossasia.susi.ai.skills.skillListing.contract.ISkillListingPresenter
import org.fossasia.susi.ai.skills.skillListing.contract.ISkillListingView
import retrofit2.Response

/**
 * Skill Listing Presenter
 *
 * Created by chiragw15 on 15/8/17.
 */
class SkillListingPresenter(val skillsActivity: SkillsActivity): ISkillListingPresenter,
        ISkillListingModel.onFetchGroupsFinishedListener, ISkillListingModel.onFetchSkillsFinishedListener {

    var skillListingModel: ISkillListingModel = SkillListingModel()
    var utilModel: IUtilModel = UtilModel(skillsActivity)
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
            skillListingView?.displayErrorDialog()
        }
    }

    override fun onGroupFetchFailure(t: Throwable) {
        skillListingView?.displayErrorDialog()
    }

    override fun onSkillFetchSuccess(response: Response<ListSkillsResponse>, group: String) {
        if (response.isSuccessful && response.body() != null) {
            skills.add(Pair(group, response.body().skillMap))
            count++
            if(count == groupsCount) {
                skillListingView?.visibilityProgressBar(false)
                skillListingView?.updateAdapter(skills)
            } else {
                skillListingModel.fetchSkills(groups[count], this)
            }
        } else {
            skillListingView?.displayErrorDialog()
        }
    }

    override fun onSkillFetchFailure(t: Throwable) {
        skillListingView?.displayErrorDialog()
    }
}