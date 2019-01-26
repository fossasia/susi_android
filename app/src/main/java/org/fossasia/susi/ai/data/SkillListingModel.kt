package org.fossasia.susi.ai.data

import org.fossasia.susi.ai.data.contract.ISkillListingModel
import org.fossasia.susi.ai.dataclasses.SkillMetricsDataQuery
import org.fossasia.susi.ai.dataclasses.SkillsListQuery
import org.fossasia.susi.ai.rest.ClientBuilder
import org.fossasia.susi.ai.rest.responses.susi.ListGroupsResponse
import org.fossasia.susi.ai.rest.responses.susi.ListSkillMetricsResponse
import org.fossasia.susi.ai.rest.responses.susi.ListSkillsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

/**
 *
 * Created by chiragw15 on 16/8/17.
 */
class SkillListingModel : ISkillListingModel {

    private lateinit var authResponseCallGroups: Call<ListGroupsResponse>
    private lateinit var authResponseCallSkills: Call<ListSkillsResponse>
    private lateinit var authResponseCallMetrics: Call<ListSkillMetricsResponse>

    override fun fetchGroups(listener: ISkillListingModel.OnFetchGroupsFinishedListener) {

        authResponseCallGroups = ClientBuilder.susiApi.fetchListGroups()

        authResponseCallGroups.enqueue(object : Callback<ListGroupsResponse> {
            override fun onResponse(call: Call<ListGroupsResponse>, response: Response<ListGroupsResponse>) {
                listener.onGroupFetchSuccess(response)
            }

            override fun onFailure(call: Call<ListGroupsResponse>, t: Throwable) {
                Timber.e(t)
                listener.onGroupFetchFailure(t)
            }
        })
    }

    override fun fetchSkills(group: String, language: String, listener: ISkillListingModel.OnFetchSkillsFinishedListener) {

        val queryObject = SkillsListQuery(group, language, "true", "descending", "rating")
        authResponseCallSkills = ClientBuilder.fetchListSkillsCall(queryObject)

        authResponseCallSkills.enqueue(object : Callback<ListSkillsResponse> {
            override fun onResponse(call: Call<ListSkillsResponse>, response: Response<ListSkillsResponse>) {
                listener.onSkillFetchSuccess(response, group)
            }

            override fun onFailure(call: Call<ListSkillsResponse>, t: Throwable) {
                Timber.e(t)
                listener.onSkillFetchFailure(t)
            }
        })
    }

    override fun fetchSkillMetrics(query: SkillMetricsDataQuery, listener: ISkillListingModel.OnFetchSkillMetricsFinishedListener) {

        authResponseCallMetrics = ClientBuilder.susiApi.fetchSkillMetricsData(query.model, query.language)

        authResponseCallMetrics.enqueue(object : Callback<ListSkillMetricsResponse> {
            override fun onResponse(call: Call<ListSkillMetricsResponse>, response: Response<ListSkillMetricsResponse>) {
                listener.onSkillMetricsFetchSuccess(response)
            }

            override fun onFailure(call: Call<ListSkillMetricsResponse>, t: Throwable) {
                Timber.e(t)
                listener.onSkillMetricsFetchFailure(t)
            }
        })
    }

    override fun cancelFetch() {
        try {
            authResponseCallGroups.cancel()
            authResponseCallSkills.cancel()
            authResponseCallMetrics.cancel()
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}
