package org.fossasia.susi.ai.data

import android.util.Log
import org.fossasia.susi.ai.data.contract.IChatModel
import org.fossasia.susi.ai.rest.ClientBuilder
import org.fossasia.susi.ai.rest.clients.LocationClient
import org.fossasia.susi.ai.rest.responses.others.LocationResponse
import org.fossasia.susi.ai.rest.responses.susi.SusiResponse
import org.fossasia.susi.ai.rest.services.LocationService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 *
 * Created by chiragw15 on 9/7/17.
 */
class ChatModel : IChatModel {

    val TAG: String = ChatModel::class.java.name

    var clientBuilder: ClientBuilder = ClientBuilder()

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

    override fun getSusiMessage(timezoneOffset: Int, longitude: Double, latitude: Double, source: String,
                                language: String, query: String, listener: IChatModel.OnMessageFromSusiReceivedListener) {

        clientBuilder.susiApi.getSusiResponse(timezoneOffset, longitude, latitude, source, Locale.getDefault().language, query).enqueue(
                object : Callback<SusiResponse> {
                    override fun onResponse(call: Call<SusiResponse>, response: Response<SusiResponse>?) {
                        listener.onSusiMessageReceivedSuccess(response)
                    }

                    override fun onFailure(call: Call<SusiResponse>, t: Throwable) {
                        if (t.localizedMessage != null) {
                            Log.d(TAG, t.localizedMessage)
                        } else {
                            Log.d(TAG, "An error occurred", t)
                        }
                        listener.onSusiMessageReceivedFailure(t)
                    }
                })
    }
}