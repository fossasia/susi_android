package org.fossasia.susi.ai.device.contract

import android.content.Context
import android.net.wifi.ScanResult

interface IDevicePresenter {

    fun onAttach(deviceView: IDeviceView)

    fun searchDevices()

    fun getAvailableDevices()

    fun onDetach()

    fun isPermissionGranted(b:Boolean)

    fun checkLocationEnabled()

    fun inflateList(list : List<ScanResult>)
}