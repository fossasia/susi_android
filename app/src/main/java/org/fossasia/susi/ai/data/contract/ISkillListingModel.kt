package org.fossasia.susi.ai.data.contract

import org.fossasia.susi.ai.dataclasses.SkillMetricsDataQuery
import org.fossasia.susi.ai.rest.responses.susi.ListGroupsResponse
import org.fossasia.susi.ai.rest.responses.susi.ListSkillMetricsResponse
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

    interface OnFetchSkillMetricsFinishedListener {
        fun onSkillMetricsFetchSuccess(response: Response<ListSkillMetricsResponse>)
        fun onSkillMetricsFetchFailure(t: Throwable)
    }

    fun fetchGroups(listener: OnFetchGroupsFinishedListener)

    fun fetchSkills(group: String, language: String, filter_name: String, filter_type: String, duration: String, listener: OnFetchSkillsFinishedListener)

    fun fetchSkillMetrics(query: SkillMetricsDataQuery, listener: OnFetchSkillMetricsFinishedListener)

    fun cancelFetch()
}
