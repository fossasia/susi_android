package org.fossasia.susi.ai.skills.skillListing

import android.util.Log
import org.fossasia.susi.ai.data.SkillListingModel
import org.fossasia.susi.ai.data.UtilModel
import org.fossasia.susi.ai.data.contract.ISkillListingModel
import org.fossasia.susi.ai.data.contract.IUtilModel
import org.fossasia.susi.ai.rest.responses.susi.ListGroupsResponse
import org.fossasia.susi.ai.rest.responses.susi.ListSkillsResponse
import org.fossasia.susi.ai.skills.SkillsActivity
import org.fossasia.susi.ai.skills.skillListing.contract.ISkillListingPresenter
import org.fossasia.susi.ai.skills.skillListing.contract.ISkillListingView
import retrofit2.Response

/**
 *
 * Created by chiragw15 on 15/8/17.
 */
class SkillListingPresenter(skillsActivity: SkillsActivity): ISkillListingPresenter,
        ISkillListingModel.onFetchGroupsFinishedListener, ISkillListingModel.onFetchSkillsFinishedListener {

    var skillListingModel: ISkillListingModel = SkillListingModel()
    var utilModel: IUtilModel = UtilModel(skillsActivity)
    var skillsListingView: ISkillListingView?= null

    override fun onAttach(skillListingView: ISkillListingView) {
        this.skillsListingView = skillsListingView
    }

    override fun getGroups() {
        skillsListingView?.visibilityProgressBar(true)
        skillListingModel.fetchGroups(this)
    }

    override fun onGroupFetchSuccess(response: Response<ListGroupsResponse>) {
        if (response.isSuccessful && response.body() != null) {
            for(group in response.body().groups) {
                skillListingModel.fetchSkills(group, this)
            }
        }
    }

    override fun onGroupFetchFailure(t: Throwable) {

    }

    override fun onSkillFetchSuccess(response: Response<ListSkillsResponse>) {
        skillsListingView?.visibilityProgressBar(false)
        if (response.isSuccessful && response.body() != null) {
            Log.v("chirag","Group " + response.body().group)
            for (skill in response.body().skills.skillMap) {
                Log.v("chirag",skill.key + ": " + skill.value.skillName)
            }
        }
    }

    override fun onSkillFetchFailure(t: Throwable) {

    }

}