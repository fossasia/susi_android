package org.fossasia.susi.ai.data

import org.fossasia.susi.ai.data.contract.IDeviceModel
import org.fossasia.susi.ai.rest.clients.DeviceClient
import org.fossasia.susi.ai.rest.responses.others.SpeakerAuthResponse
import org.fossasia.susi.ai.rest.responses.others.SpeakerConfigResponse
import org.fossasia.susi.ai.rest.responses.others.SpeakerWifiResponse
import org.fossasia.susi.ai.rest.services.DeviceService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class DeviceModel : IDeviceModel {

    private val deviceService = DeviceClient.getClient().create(DeviceService::class.java)

    override fun sendWifiCredentials(ssid: String, pass: String, listener: IDeviceModel.onSendWifiCredentialsListener) {

        deviceService.wifiCredentials(ssid, pass).enqueue(object : Callback<SpeakerWifiResponse> {
            override fun onFailure(call: Call<SpeakerWifiResponse>, t: Throwable?) {
                if (t?.localizedMessage != null) {
                    Timber.d(t.localizedMessage)
                } else {
                    Timber.d(t, "An error occurred")
                }
                Timber.d("Error in WiFi : " + call.toString())
                if (t != null)
                    listener.onSendCredentialFailure(t.localizedMessage)
            }

            override fun onResponse(call: Call<SpeakerWifiResponse>, response: Response<SpeakerWifiResponse>) {
                listener.onSendCredentialSuccess()
            }
        })
    }

    override fun setConfiguration(stt: String, tts: String, hotword: String, wake: String, listener: IDeviceModel.onSetConfigurationListener) {
        val query: MutableMap<String, String> = HashMap()
        query.put("stt", stt)
        query.put("tts", tts)
        query.put("hotword", hotword)
        query.put("wake", wake)

        deviceService.ttSSettings(query).enqueue(object : Callback<SpeakerConfigResponse> {
            override fun onFailure(call: Call<SpeakerConfigResponse>, t: Throwable?) {
                if (t?.localizedMessage != null) {
                    Timber.d(t.cause)
                } else {
                    Timber.d(t, "An error occurred")
                }
                Timber.d("Error in Configuration : " + call.toString())
                if (t != null)
                    listener.onSetConfigFailure(t.localizedMessage)
            }

            override fun onResponse(call: Call<SpeakerConfigResponse>, response: Response<SpeakerConfigResponse>) {
                listener.onSetConfigSuccess()
            }

        })
    }

    override fun sendAuthCredentials(choice: String, email: String, password: String, listener: IDeviceModel.onSendAuthCredentialsListener) {

        deviceService.authCredentials(choice, email, password).enqueue(object : Callback<SpeakerAuthResponse> {
            override fun onFailure(call: Call<SpeakerAuthResponse>, t: Throwable?) {
                if (t?.localizedMessage != null) {
                    Timber.d(t.localizedMessage)
                } else {
                    Timber.d(t, "An error occurred")
                }
                Timber.d("Error in Authentication : " + call.toString())
                if (t != null)
                    listener.onSendAuthFailure(t.localizedMessage)
            }

            override fun onResponse(call: Call<SpeakerAuthResponse>, response: Response<SpeakerAuthResponse>) {
                listener.onSendAuthSuccess()
            }

        })
    }
}