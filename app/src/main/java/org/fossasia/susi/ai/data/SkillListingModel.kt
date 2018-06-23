package org.fossasia.susi.ai.data

import org.fossasia.susi.ai.data.contract.ISkillListingModel
import org.fossasia.susi.ai.rest.ClientBuilder
import org.fossasia.susi.ai.rest.responses.susi.ListGroupsResponse
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

    private var clientBuilder: ClientBuilder = ClientBuilder()

    override fun fetchGroups(listener: ISkillListingModel.OnFetchGroupsFinishedListener) {

        authResponseCallGroups = clientBuilder.susiApi.fetchListGroups()

        authResponseCallGroups.enqueue(object : Callback<ListGroupsResponse> {
            override fun onResponse(call: Call<ListGroupsResponse>, response: Response<ListGroupsResponse>) {
                listener.onGroupFetchSuccess(response)
            }

            override fun onFailure(call: Call<ListGroupsResponse>, t: Throwable) {
                t.printStackTrace()
                listener.onGroupFetchFailure(t)
            }
        })
    }

    override fun fetchSkills(group: String, listener: ISkillListingModel.OnFetchSkillsFinishedListener) {

        authResponseCallSkills = clientBuilder.susiApi.fetchListSkills(group, "true", "descending", "rating")

        authResponseCallSkills.enqueue(object : Callback<ListSkillsResponse> {
            override fun onResponse(call: Call<ListSkillsResponse>, response: Response<ListSkillsResponse>) {
                listener.onSkillFetchSuccess(response, group)
            }

            override fun onFailure(call: Call<ListSkillsResponse>, t: Throwable) {
                t.printStackTrace()
                listener.onSkillFetchFailure(t)
            }
        })
    }

    override fun cancelFetch() {
        try {
            authResponseCallGroups.cancel()
            authResponseCallSkills.cancel()
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}