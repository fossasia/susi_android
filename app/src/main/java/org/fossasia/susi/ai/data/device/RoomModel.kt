package org.fossasia.susi.ai.data.device

import org.fossasia.susi.ai.data.contract.IRoomModel
import org.fossasia.susi.ai.dataclasses.AddDeviceQuery
import org.fossasia.susi.ai.rest.ClientBuilder
import org.fossasia.susi.ai.rest.responses.susi.GetAddDeviceResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class RoomModel : IRoomModel {
    private lateinit var addDeviceResponseCall: Call<GetAddDeviceResponse>

    override fun addDeviceToServer(query: AddDeviceQuery) {
        addDeviceResponseCall = ClientBuilder.addDeviceCall(query)

        addDeviceResponseCall.enqueue(object : Callback<GetAddDeviceResponse> {
            override fun onFailure(call: Call<GetAddDeviceResponse>, t: Throwable) {
                Timber.d("KHANKI Failed " + t)
            }

            override fun onResponse(call: Call<GetAddDeviceResponse>, response: Response<GetAddDeviceResponse>) {
                Timber.d("KHANKI Success " + response)
            }
        })
    }
}