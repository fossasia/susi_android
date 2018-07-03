package org.fossasia.susi.ai.data

import org.fossasia.susi.ai.data.contract.IDeviceModel
import org.fossasia.susi.ai.rest.clients.DeviceClient
import org.fossasia.susi.ai.rest.services.DeviceService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class DeviceModel : IDeviceModel {

    private val deviceService = DeviceClient.getClient().create(DeviceService::class.java)

    override fun sendWifiCredentials(ssid: String, pass: String, listener: IDeviceModel.onSendWifiCredentialsListener) {

        deviceService.wifiCredentials(ssid, pass).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>?, t: Throwable?) {
                if (t?.localizedMessage != null) {
                    Timber.d(t.localizedMessage)
                } else {
                    Timber.d(t, "An error occurred")
                }
                listener.onSendCredentialFailure()
            }

            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                listener.onSendCredentialSuccess()
            }
        })
    }

    override fun setConfiguration(stt: String, tts: String, hotword: String, wake: String, listener: IDeviceModel.onSetConfigurationListener) {

        deviceService.ttSSettings(stt, tts, hotword, wake).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>?, t: Throwable?) {
                if (t?.localizedMessage != null) {
                    Timber.d(t.localizedMessage)
                } else {
                    Timber.d(t, "An error occurred")
                }
                listener.onSetConfigFailure()
            }

            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                listener.onSetConfigSuccess()
            }

        })
    }

    override fun sendAuthCredentials(choice: String, email: String, password: String, listener: IDeviceModel.onSendAuthCredentialsListener) {

        deviceService.authCredentials(choice, email, password).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>?, t: Throwable?) {
                if (t?.localizedMessage != null) {
                    Timber.d(t.localizedMessage)
                } else {
                    Timber.d(t, "An error occurred")
                }
                listener.onSendAuthFailure()
            }

            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                listener.onSendAuthSuccess()
            }

        })
    }
}