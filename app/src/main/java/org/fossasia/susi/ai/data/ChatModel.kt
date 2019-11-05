package org.fossasia.susi.ai.data

import org.fossasia.susi.ai.data.contract.IChatModel
import org.fossasia.susi.ai.rest.ClientBuilder
import org.fossasia.susi.ai.rest.clients.LocationClient
import org.fossasia.susi.ai.rest.responses.others.LocationResponse
import org.fossasia.susi.ai.rest.responses.susi.MemoryResponse
import org.fossasia.susi.ai.rest.responses.susi.SusiResponse
import org.fossasia.susi.ai.rest.services.LocationService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class ChatModel : IChatModel {

    override fun retrieveOldMessages(listener: IChatModel.OnRetrievingMessagesFinishedListener) {
        val call = ClientBuilder.susiApi.chatHistory
        call.enqueue(object : Callback<MemoryResponse> {
            override fun onResponse(call: Call<MemoryResponse>, response: Response<MemoryResponse>?) {
                listener.onRetrieveSuccess(response)
            }

            override fun onFailure(call: Call<MemoryResponse>, t: Throwable) {
                Timber.e(t)
                listener.onRetrieveFailure()
            }
        })
    }

    override fun getLocationFromIP(listener: IChatModel.OnLocationFromIPReceivedListener) {
        val locationService = LocationClient.retrofit.create(LocationService::class.java)

        locationService.locationUsingIP.enqueue(object : Callback<LocationResponse> {
            override fun onResponse(call: Call<LocationResponse>, response: Response<LocationResponse>) {
                listener.onLocationSuccess(response)
            }

            override fun onFailure(call: Call<LocationResponse>, t: Throwable) {
                Timber.e(t)
            }
        })
    }

    override fun getSusiMessage(map: Map<String, String?>, listener: IChatModel.OnMessageFromSusiReceivedListener) {

        ClientBuilder.susiApi.getSusiResponse(map).enqueue(
                object : Callback<SusiResponse> {
                    override fun onResponse(call: Call<SusiResponse>, response: Response<SusiResponse>?) {
                        listener.onSusiMessageReceivedSuccess(response)
                    }

                    override fun onFailure(call: Call<SusiResponse>, t: Throwable) {
                        if (t.localizedMessage != null) {
                            Timber.d(t.localizedMessage)
                        } else {
                            Timber.d(t, "An error occurred")
                        }
                        listener.onSusiMessageReceivedFailure(t)
                    }
                })
    }
}
