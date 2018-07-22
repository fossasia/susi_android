package org.fossasia.susi.ai.data

import org.fossasia.susi.ai.data.contract.IDeviceModel
import org.fossasia.susi.ai.dataclasses.SpeakerConfiguration

interface DeviceService {

    fun wifiCredentials(ssid: String, pass: String, listener: IDeviceModel.onSendWifiCredentialsListener)

    fun ttsSettings(speakerConfig: SpeakerConfiguration, listener: IDeviceModel.onSetConfigurationListener)

    fun authCredentials(choice: String, email: String, password: String, listener: IDeviceModel.onSendAuthCredentialsListener)
}