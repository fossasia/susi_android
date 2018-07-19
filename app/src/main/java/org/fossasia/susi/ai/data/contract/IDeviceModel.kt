package org.fossasia.susi.ai.data.contract

import org.fossasia.susi.ai.dataclasses.SpeakerConfiguration

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

    fun sendWifiCredentials(ssid: String, pass: String, listener: onSendWifiCredentialsListener)

    fun setConfiguration(speakerConfig: SpeakerConfiguration, listener: onSetConfigurationListener)

    fun sendAuthCredentials(choice: String, email: String, password: String, listener: onSendAuthCredentialsListener)
}