package org.fossasia.susi.ai.data

import org.fossasia.susi.ai.data.contract.IDeviceModel
import org.fossasia.susi.ai.dataclasses.SpeakerConfiguration

class DeviceModel : IDeviceModel {

    private val deviceService: DeviceService = DeviceServiceImpl()
    override fun sendWifiCredentials(ssid: String, pass: String, listener: IDeviceModel.onSendWifiCredentialsListener) {
        deviceService.wifiCredentials(ssid, pass, listener)
    }

    override fun setConfiguration(speakerConfig: SpeakerConfiguration, listener: IDeviceModel.onSetConfigurationListener) {
        deviceService.ttsSettings(speakerConfig, listener)
    }

    override fun sendAuthCredentials(choice: String, email: String, password: String, listener: IDeviceModel.onSendAuthCredentialsListener) {
        deviceService.authCredentials(choice, email, password, listener)
    }
}