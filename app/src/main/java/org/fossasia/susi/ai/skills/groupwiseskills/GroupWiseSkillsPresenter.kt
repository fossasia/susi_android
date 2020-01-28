package org.fossasia.susi.ai.skills.groupwiseskills

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import org.fossasia.susi.ai.data.GroupWiseSkillsModel
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
import retrofit2.Response
import timber.log.Timber

/**
 *
 * Created by arundhati24 on 16/07/2018.
 */
class GroupWiseSkillsPresenter(val groupWiseSkillsFragment: GroupWiseSkillsFragment) : IGroupWiseSkillsPresenter,
        IGroupWiseSkillsModel.OnFetchSkillsFinishedListener {
    private var groupWiseSkillsModel: IGroupWiseSkillsModel = GroupWiseSkillsModel()
    private var groupWiseSkillsView: IGroupWiseSkillsView? = null
    private var skills = GroupWiseSkills("", ArrayList())
    private val _swipeRefreshController = MutableLiveData<Boolean>()
    val swipeRefreshController: LiveData<Boolean>
        get() = _swipeRefreshController
    private val _shimmerController = MutableLiveData<Boolean>()
    val shimmerController: MutableLiveData<Boolean>
        get() = _shimmerController

    override fun onAttach(groupWiseSkillsView: IGroupWiseSkillsView) {
        this.groupWiseSkillsView = groupWiseSkillsView
    }

    override fun getSkills(swipeToRefreshActive: Boolean, group: String) {
        _swipeRefreshController.value = swipeToRefreshActive
        groupWiseSkillsModel.fetchSkills(group, PrefManager.getString(Constant.LANGUAGE, Constant.DEFAULT), FILTER_NAME, FILTER_TYPE, DURATION, this)
    }

    override fun onSkillFetchSuccess(response: Response<ListSkillsResponse>, group: String) {
        _shimmerController.value = false
        _swipeRefreshController.value = false
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
            _shimmerController.value = false
            _swipeRefreshController.value = false
            groupWiseSkillsView?.displayError()
        }
    }

    override fun onSkillFetchFailure(t: Throwable) {
        _shimmerController.value = false
        _swipeRefreshController.value = false
        groupWiseSkillsView?.displayError()
    }

    override fun onDetach() {
        groupWiseSkillsModel.cancelFetch()
        groupWiseSkillsView = null
    }
}
