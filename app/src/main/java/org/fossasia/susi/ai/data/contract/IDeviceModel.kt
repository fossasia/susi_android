package org.fossasia.susi.ai.data.contract

import org.fossasia.susi.ai.data.device.SpeakerAuth
import org.fossasia.susi.ai.data.device.SpeakerConfiguration
import org.fossasia.susi.ai.rest.responses.others.AddRoomResponse
import retrofit2.Response

interface IDeviceModel {

    interface onSendWifiCredentialsListener {
        fun onSendCredentialSuccess()
        fun onSendCredentialFailure(localMessage: String)
    }

    interface onSendAuthCredentialsListener {
        fun onSendAuthSuccess()
        fun onSendAuthFailure(localMessage: String)
    }

    interface onSetConfigurationListener {
        fun onSetConfigSuccess()
        fun onSetConfigFailure(localMessage: String)
    }

    interface onSendRoomDetails {
        fun onSendRoomSuccess(roomResponse: Response<AddRoomResponse>)
        fun onSendRoomFailure(localMessage: String)
    }

    fun sendWifiCredentials(ssid: String, pass: String, listener: onSendWifiCredentialsListener)

    fun setConfiguration(speakerConfig: SpeakerConfiguration, listener: onSetConfigurationListener)

    fun sendAuthCredentials(speakerAuth: SpeakerAuth, listener: onSendAuthCredentialsListener)

    fun sendRoomDetails(room_name: String, listener: onSendRoomDetails)
}
