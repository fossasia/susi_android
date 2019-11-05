package org.fossasia.susi.ai.device.deviceconnect.contract

import android.net.wifi.ScanResult

interface IDeviceConnectPresenter {

    fun onAttach(deviceConnectView: IDeviceConnectView)

    fun availableWifi(list: List<ScanResult>)

    fun onDetach()

    fun isPermissionGranted(b: Boolean)

    fun checkLocationEnabled()

    fun checkWifiEnabled()

    fun availableDevices(list: List<ScanResult>)

    fun connectToDevice(networkSSID: String)

    fun makeConnectionRequest()

    fun searchWiFi()

    fun makeWifiRequest(ssid: String, password: String)

    fun makeConfigRequest()

    fun makeAuthRequest(password: String)

    fun disconnectConnectedWifi()

    fun addRoom(room: String)

    fun deleteRoom(room: String?)

    fun getSUSIAIConnectionInfo(): Boolean

    fun selectedRoom(roomName: String?)

    fun makeAddRoomRequest(room_name: String)

    fun selectedWifi(wifiName: String)
}
