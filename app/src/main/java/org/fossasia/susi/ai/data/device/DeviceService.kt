package org.fossasia.susi.ai.data.device

import org.fossasia.susi.ai.data.contract.IDeviceModel

interface DeviceService {

    fun submitWifiCredentials(ssid: String, pass: String, listener: IDeviceModel.onSendWifiCredentialsListener)

    fun submitConfigSettings(speakerConfig: SpeakerConfiguration, listener: IDeviceModel.onSetConfigurationListener)

    fun submitAuthCredentials(speakerAuth: SpeakerAuth, listener: IDeviceModel.onSendAuthCredentialsListener)

    fun submitRoomDetails(roomName: String, listener: IDeviceModel.onSendRoomDetails)
}
