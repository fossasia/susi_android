package org.fossasia.susi.ai.skills.skillListing

import android.os.Handler
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
import java.lang.Thread.sleep

/**
 *
 * Created by chiragw15 on 15/8/17.
 */
class SkillListingPresenter(skillsActivity: SkillsActivity): ISkillListingPresenter,
        ISkillListingModel.onFetchGroupsFinishedListener, ISkillListingModel.onFetchSkillsFinishedListener {

    var skillListingModel: ISkillListingModel = SkillListingModel()
    var utilModel: IUtilModel = UtilModel(skillsActivity)
    var skillListingView: ISkillListingView ?= null
    var count = 0
    var skills: ArrayList<Pair<String, Map<String, SkillData>>> = ArrayList()
    var groupsCount = 0

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

            for(group in response.body().groups) {
                val handler = Handler()
                val runnable = object : Runnable {
                    override fun run() {
                        skillListingModel.fetchSkills(group, this@SkillListingPresenter)
                        handler.postDelayed(this, 1000)
                    }
                }
                runnable.run()
            }
        }
    }

    override fun onGroupFetchFailure(t: Throwable) {

    }

    override fun onSkillFetchSuccess(response: Response<ListSkillsResponse>, group: String) {
        skillListingView?.visibilityProgressBar(false)
        if (response.isSuccessful && response.body() != null) {

            skills.add(Pair(group, response.body().skillMap))
            count++

            if(count == groupsCount) {
                skillListingView?.setAdapter(skills)
            }
        }
    }

    override fun onSkillFetchFailure(t: Throwable) {

    }

}