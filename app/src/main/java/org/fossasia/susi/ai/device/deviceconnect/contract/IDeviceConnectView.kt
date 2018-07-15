package org.fossasia.susi.ai.device.deviceconnect.contract


interface IDeviceConnectView {

    fun setupDeviceAdapter(scanList: List<String>)

    fun showProgress(title: String?)

    fun onDeviceConnectionError(title: String?, content: String?)

    fun stopProgress()

    fun showLocationIntentDialog()

    fun askForPermissions()

    fun startScan(isDevice: Boolean)

    fun unregister()

    fun onDeviceConnectionSuccess()

    fun setupWiFiAdapter(scanList: ArrayList<String>)

}