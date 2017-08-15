package org.fossasia.susi.ai.data

import org.fossasia.susi.ai.data.contract.ISkillListingModel
import org.fossasia.susi.ai.rest.ClientBuilder
import org.fossasia.susi.ai.rest.responses.susi.ListGroupsResponse
import org.fossasia.susi.ai.rest.responses.susi.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 *
 * Created by chiragw15 on 16/8/17.
 */
class SkillListingModel: ISkillListingModel {

    lateinit var authResponseCall: Call<ListGroupsResponse>

    override fun fetchGroups(listener: ISkillListingModel.onFetchGroupsFinishedListener) {

        authResponseCall = ClientBuilder().susiApi.fetchListGroups()

        authResponseCall.enqueue(object : Callback<ListGroupsResponse> {
            override fun onResponse(call: Call<ListGroupsResponse>, response: Response<ListGroupsResponse>) {
                listener.onGroupFetchSuccess(response)
            }

            override fun onFailure(call: Call<ListGroupsResponse>, t: Throwable) {
                t.printStackTrace()
                listener.onGroupFetchFailure(t)
            }
        })
    }
}