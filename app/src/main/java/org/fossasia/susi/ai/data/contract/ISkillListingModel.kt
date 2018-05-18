package org.fossasia.susi.ai.data.contract

import org.fossasia.susi.ai.rest.responses.susi.ListGroupsResponse
import org.fossasia.susi.ai.rest.responses.susi.ListSkillsResponse
import retrofit2.Response

/**
 *
 * Created by chiragw15 on 16/8/17.
 */
interface ISkillListingModel {
    interface OnFetchGroupsFinishedListener {
        fun onGroupFetchSuccess(response: Response<ListGroupsResponse>)
        fun onGroupFetchFailure(t: Throwable)
    }

    interface OnFetchSkillsFinishedListener {
        fun onSkillFetchSuccess(response: Response<ListSkillsResponse>, group: String)
        fun onSkillFetchFailure(t: Throwable)
    }

    fun fetchGroups(listener: OnFetchGroupsFinishedListener)

    fun fetchSkills(group: String, listener: OnFetchSkillsFinishedListener)

    fun cancelFetch()
}