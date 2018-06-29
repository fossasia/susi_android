package org.fossasia.susi.ai.data

import org.fossasia.susi.ai.data.contract.IChatModel
import org.fossasia.susi.ai.rest.ClientBuilder
import org.fossasia.susi.ai.rest.clients.LocationClient
import org.fossasia.susi.ai.rest.responses.others.LocationResponse
import org.fossasia.susi.ai.rest.responses.susi.MemoryResponse
import org.fossasia.susi.ai.rest.responses.susi.SusiResponse
import org.fossasia.susi.ai.rest.responses.susi.TableSusiResponse
import org.fossasia.susi.ai.rest.services.LocationService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

/**
 * The Model of Chat Activity.
 *
 * The M in MVP
 * Created by chiragw15 on 9/7/17.
 */
class ChatModel : IChatModel {

    private var clientBuilder: ClientBuilder = ClientBuilder()

    override fun retrieveOldMessages(listener: IChatModel.OnRetrievingMessagesFinishedListener) {
        val call = clientBuilder.susiApi.chatHistory
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
        val locationService = LocationClient.getClient().create(LocationService::class.java)

        locationService.locationUsingIP.enqueue(object : Callback<LocationResponse> {
            override fun onResponse(call: Call<LocationResponse>, response: Response<LocationResponse>) {
                listener.onLocationSuccess(response)
            }

            override fun onFailure(call: Call<LocationResponse>, t: Throwable) {
                Timber.e(t)
            }
        })
    }

    override fun getSusiMessage(map: Map<String, String>, listener: IChatModel.OnMessageFromSusiReceivedListener) {

        clientBuilder.susiApi.getSusiResponse(map).enqueue(
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

    override fun getTableSusiMessage(map: Map<String, String>, listener: IChatModel.OnMessageFromSusiReceivedListener) {

        clientBuilder.susiApi.getTableSusiResponse(map).enqueue(
                object : Callback<TableSusiResponse> {
                    override fun onResponse(call: Call<TableSusiResponse>, response: Response<TableSusiResponse>?) {
                        listener.onTableMessageReceivedSuccess(response)
                    }

                    override fun onFailure(call: Call<TableSusiResponse>, t: Throwable) {
                        if (t.localizedMessage != null) {
                            Timber.d(t.localizedMessage)
                        } else {
                            Timber.d(t, "An error occurred")
                        }
                        // listener.onSusiMessageReceivedFailure(t)
                    }
                })
    }
}