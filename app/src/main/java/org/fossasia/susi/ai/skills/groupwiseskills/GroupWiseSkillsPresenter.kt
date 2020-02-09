package org.fossasia.susi.ai.skills.groupwiseskills

import org.fossasia.susi.ai.data.contract.IGroupWiseSkillsModel
import org.fossasia.susi.ai.dataclasses.GroupWiseSkills
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.PrefManager
import org.fossasia.susi.ai.rest.responses.susi.ListSkillsResponse
import org.fossasia.susi.ai.rest.responses.susi.SkillData
import org.fossasia.susi.ai.skills.SkillsActivity.Companion.DURATION
import org.fossasia.susi.ai.skills.SkillsActivity.Companion.FILTER_NAME
import org.fossasia.susi.ai.skills.SkillsActivity.Companion.FILTER_TYPE
import org.fossasia.susi.ai.skills.groupwiseskills.contract.IGroupWiseSkillsPresenter
import org.fossasia.susi.ai.skills.groupwiseskills.contract.IGroupWiseSkillsView
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf
import retrofit2.Response
import timber.log.Timber

/**
 *
 * Created by arundhati24 on 16/07/2018.
 */
class GroupWiseSkillsPresenter(private val groupWiseSkillsView: IGroupWiseSkillsView) : IGroupWiseSkillsPresenter,
        IGroupWiseSkillsModel.OnFetchSkillsFinishedListener, KoinComponent {
    private val groupWiseSkillsModel: IGroupWiseSkillsModel by inject { parametersOf(this) }
    private var skills = GroupWiseSkills("", ArrayList())

    override fun onAttach(groupWiseSkillsView: IGroupWiseSkillsView) {
    }

    override fun getSkills(swipeToRefreshActive: Boolean, group: String) {
        groupWiseSkillsView?.visibilityProgressBar(!swipeToRefreshActive)
        groupWiseSkillsModel.fetchSkills(group, PrefManager.getString(Constant.LANGUAGE, Constant.DEFAULT), FILTER_NAME, FILTER_TYPE, DURATION)
    }

    override fun onSkillFetchSuccess(response: Response<ListSkillsResponse>, group: String) {
        groupWiseSkillsView?.visibilityProgressBar(false)
        if (response.isSuccessful && response.body() != null) {
            Timber.d("GROUP WISE SKILLS FETCHED")
            val responseSkillList = response.body()?.filteredSkillsData
            if (responseSkillList != null && responseSkillList.isNotEmpty()) {
                skills.skillsList.clear()
                if (responseSkillList is MutableList<SkillData>) skills.skillsList = responseSkillList
                groupWiseSkillsView?.updateAdapter(skills)
            } else {
                groupWiseSkillsView?.showEmptySkillsListMessage()
            }
        } else {
            Timber.d("GROUP WISE SKILLS NOT FETCHED")
            groupWiseSkillsView?.visibilityProgressBar(false)
            groupWiseSkillsView?.displayError()
        }
    }

    override fun onSkillFetchFailure(t: Throwable) {
        groupWiseSkillsView?.visibilityProgressBar(false)
        groupWiseSkillsView?.displayError()
    }

    override fun onDetach() {
        groupWiseSkillsModel.cancelFetch()
    }
}
