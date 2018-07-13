package org.fossasia.susi.ai.data.contract

interface IDeviceModel {

    interface onSendWifiCredentialsListener {
        fun onSendCredentialSuccess()
        fun onSendCredentialFailure()
    }

    interface onSendAuthCredentialsListener {
        fun onSendAuthSuccess()
        fun onSendAuthFailure()
    }

    interface onSetConfigurationListener {
        fun onSetConfigSuccess()
        fun onSetConfigFailure()
    }

    fun sendWifiCredentials(ssid: String, pass: String, listener: onSendWifiCredentialsListener)

    fun setConfiguration(stt: String, tts: String, hotword: String, wake: String, listener: onSetConfigurationListener)

    fun sendAuthCredentials(choice: String, email: String, password: String, listener: onSendAuthCredentialsListener)
}