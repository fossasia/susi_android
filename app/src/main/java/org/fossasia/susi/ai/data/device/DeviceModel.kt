package org.fossasia.susi.ai.data.device

import org.fossasia.susi.ai.data.contract.IDeviceModel

class DeviceModel : IDeviceModel {

    private val deviceService: DeviceService = DeviceServiceImpl()
    override fun sendWifiCredentials(ssid: String, pass: String, listener: IDeviceModel.onSendWifiCredentialsListener) {
        deviceService.submitWifiCredentials(ssid, pass, listener)
    }

    override fun setConfiguration(speakerConfig: SpeakerConfiguration, listener: IDeviceModel.onSetConfigurationListener) {
        deviceService.submitConfigSettings(speakerConfig, listener)
    }

    override fun sendAuthCredentials(speakerAuth: SpeakerAuth, listener: IDeviceModel.onSendAuthCredentialsListener) {
        deviceService.submitAuthCredentials(speakerAuth, listener)
    }

    override fun sendRoomDetails(room_name: String, listener: IDeviceModel.onSendRoomDetails) {
        deviceService.submitRoomDetails(room_name, listener)
    }
}
