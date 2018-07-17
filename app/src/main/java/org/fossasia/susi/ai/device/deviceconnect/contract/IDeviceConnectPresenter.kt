package org.fossasia.susi.ai.device.deviceconnect.contract

import android.net.wifi.ScanResult

interface IDeviceConnectPresenter {

    fun onAttach(deviceConnectView: IDeviceConnectView)

    fun searchDevices()

    fun availableWifi(list: List<ScanResult>)

    fun onDetach()

    fun isPermissionGranted(b: Boolean)

    fun checkLocationEnabled()

    fun availableDevices(list: List<ScanResult>)

    fun connectToDevice(networkSSID: String)

    fun makeConnectionRequest()

    fun searchWiFi()

    fun makeWifiRequest(ssid: String, password: String)

    fun makeConfigRequest()

    fun makeAuthRequest(password: String)
}