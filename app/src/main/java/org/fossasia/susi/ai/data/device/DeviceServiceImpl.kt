package org.fossasia.susi.ai.data.device

import org.fossasia.susi.ai.data.contract.IDeviceModel
import org.fossasia.susi.ai.rest.clients.DeviceClient
import org.fossasia.susi.ai.rest.responses.others.AddRoomResponse
import org.fossasia.susi.ai.rest.responses.others.SpeakerAuthResponse
import org.fossasia.susi.ai.rest.responses.others.SpeakerConfigResponse
import org.fossasia.susi.ai.rest.responses.others.SpeakerWifiResponse
import org.fossasia.susi.ai.rest.services.DeviceApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class DeviceServiceImpl : DeviceService {
    private val deviceApi = DeviceClient.retrofit.create(DeviceApi::class.java)

    override fun submitConfigSettings(speakerConfig: SpeakerConfiguration, listener: IDeviceModel.onSetConfigurationListener) {
        val query: MutableMap<String, String> = HashMap()
        query.put("stt", speakerConfig.stt)
        query.put("tts", speakerConfig.tts)
        query.put("hotword", speakerConfig.hotword)
        query.put("wake", speakerConfig.wake)

        deviceApi.ttSSettings(query).enqueue(object : Callback<SpeakerConfigResponse> {
            override fun onFailure(call: Call<SpeakerConfigResponse>, t: Throwable?) {
                Timber.e(t, "Error in Configuration : " + call.toString())
                if (t != null)
                    listener.onSetConfigFailure(t.localizedMessage)
            }

            override fun onResponse(call: Call<SpeakerConfigResponse>, response: Response<SpeakerConfigResponse>) {
                listener.onSetConfigSuccess()
            }
        })
    }

    override fun submitAuthCredentials(speakerAuth: SpeakerAuth, listener: IDeviceModel.onSendAuthCredentialsListener) {
        deviceApi.authCredentials(speakerAuth.choice, speakerAuth.email, speakerAuth.password).enqueue(object : Callback<SpeakerAuthResponse> {
            override fun onFailure(call: Call<SpeakerAuthResponse>, t: Throwable?) {
                Timber.e(t, "Error in Authentication : " + call.toString())
                if (t != null)
                    listener.onSendAuthFailure(t.localizedMessage)
            }

            override fun onResponse(call: Call<SpeakerAuthResponse>, response: Response<SpeakerAuthResponse>) {
                listener.onSendAuthSuccess()
            }
        })
    }

    override fun submitWifiCredentials(ssid: String, pass: String, listener: IDeviceModel.onSendWifiCredentialsListener) {
        deviceApi.wifiCredentials(ssid, pass).enqueue(object : Callback<SpeakerWifiResponse> {
            override fun onFailure(call: Call<SpeakerWifiResponse>, t: Throwable?) {
                Timber.e(t, "Error in WiFi : " + call.toString())
                if (t != null)
                    listener.onSendCredentialFailure(t.localizedMessage)
            }

            override fun onResponse(call: Call<SpeakerWifiResponse>, response: Response<SpeakerWifiResponse>) {
                listener.onSendCredentialSuccess()
            }
        })
    }

    override fun submitRoomDetails(roomName: String, listener: IDeviceModel.onSendRoomDetails) {
        deviceApi.roomDetails(roomName).enqueue(object : Callback<AddRoomResponse> {
            override fun onFailure(call: Call<AddRoomResponse>, t: Throwable) {
                Timber.e(t, "Error in WiFi : " + call.toString())
                if (t != null)
                    listener.onSendRoomFailure(t.localizedMessage)
            }

            override fun onResponse(call: Call<AddRoomResponse>, response: Response<AddRoomResponse>) {
                listener.onSendRoomSuccess(response)
            }
        })
    }
}
