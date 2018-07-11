package org.fossasia.susi.ai.device.contract

import android.net.wifi.ScanResult

interface IDeviceConnectPresenter {

    fun onAttach(deviceConnectView: IDeviceConnectView)

    fun searchDevices()

    fun getAvailableDevices()

    fun onDetach()

    fun isPermissionGranted(b:Boolean)

    fun checkLocationEnabled()

    fun inflateList(list : List<ScanResult>)

    fun connectToDevice(networkSSID: String)

    fun makeConnectionRequest()
}