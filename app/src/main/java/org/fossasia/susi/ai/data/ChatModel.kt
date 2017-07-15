package org.fossasia.susi.ai.data

import android.util.Log
import org.fossasia.susi.ai.data.contract.IChatModel
import org.fossasia.susi.ai.rest.clients.LocationClient
import org.fossasia.susi.ai.rest.responses.others.LocationResponse
import org.fossasia.susi.ai.rest.services.LocationService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by chiragw15 on 9/7/17.
 */
class ChatModel : IChatModel {

    val TAG: String = ChatModel::class.java.name

    override fun retrieveOldMessages(listener: IChatModel.OnRetrievingMessagesFinishedListener) {
        val thread = object : Thread() {
            override fun run() {
                getOldMessages(listener)
            }
        }
        thread.start()
    }

    override fun getLocationFromIP(listener: IChatModel.OnLocationFromIPReceivedListener) {
        val locationService = LocationClient.getClient().create(LocationService::class.java)

        locationService.locationUsingIP.enqueue(object : Callback<LocationResponse> {
            override fun onResponse(call: Call<LocationResponse>, response: Response<LocationResponse>) {
                listener.onLocationSuccess(response)
            }

            override fun onFailure(call: Call<LocationResponse>, t: Throwable) {
                Log.e(TAG, t.toString())
            }
        })
    }

    fun getOldMessages(listener: IChatModel.OnRetrievingMessagesFinishedListener) {
    }
}