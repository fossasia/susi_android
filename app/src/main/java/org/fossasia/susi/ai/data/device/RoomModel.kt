package org.fossasia.susi.ai.data.device

import org.fossasia.susi.ai.data.contract.IRoomModel
import org.fossasia.susi.ai.dataclasses.AddDeviceQuery
import org.fossasia.susi.ai.rest.ClientBuilder
import org.fossasia.susi.ai.rest.responses.susi.ConnectedDevicesResponse
import org.fossasia.susi.ai.rest.responses.susi.GetAddDeviceResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoomModel : IRoomModel {
    private lateinit var addDeviceResponseCall: Call<GetAddDeviceResponse>
    private lateinit var connectedDeviceResponseCall: Call<ConnectedDevicesResponse>

    override fun addDeviceToServer(query: AddDeviceQuery, listener: IRoomModel.onAddDeviceListener) {
        addDeviceResponseCall = ClientBuilder.addDeviceCall(query)

        addDeviceResponseCall.enqueue(object : Callback<GetAddDeviceResponse> {
            override fun onFailure(call: Call<GetAddDeviceResponse>, t: Throwable) {
                listener.onError(t)
            }

            override fun onResponse(call: Call<GetAddDeviceResponse>, response: Response<GetAddDeviceResponse>) {
                listener.onAddDeviceSuccess(response)
            }
        })
    }

    override fun getConnectedDevices(listener: IRoomModel.onConnectedDeviceFetchingFinishedListener) {
        connectedDeviceResponseCall = ClientBuilder.susiApi.getConnectedDevices

        connectedDeviceResponseCall.enqueue(object : Callback<ConnectedDevicesResponse> {
            override fun onFailure(call: Call<ConnectedDevicesResponse>, t: Throwable) {
                listener.onError(t)
            }

            override fun onResponse(call: Call<ConnectedDevicesResponse>, response: Response<ConnectedDevicesResponse>) {
                listener.onRoomModelSuccess(response)
            }
        })
    }
}
