package org.fossasia.susi.ai.data.device

import org.fossasia.susi.ai.data.contract.IDeviceModel

interface DeviceService {

    fun submitWifiCredentials(ssid: String, pass: String, listener: IDeviceModel.onSendWifiCredentialsListener)

    fun submitConfigSettings(speakerConfig: SpeakerConfiguration, listener: IDeviceModel.onSetConfigurationListener)

    fun submitAuthCredentials(speakerAuth: SpeakerAuth, listener: IDeviceModel.onSendAuthCredentialsListener)

    fun submitRoomDetails(room_name: String, listener: IDeviceModel.onSendRoomDetails)
}