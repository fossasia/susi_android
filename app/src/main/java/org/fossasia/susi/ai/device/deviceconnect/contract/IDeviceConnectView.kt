package org.fossasia.susi.ai.device.deviceconnect.contract


interface IDeviceConnectView {

    fun setupAdapter(scanList: List<String>, isDevice: Boolean)

    fun showProgress(title: String?)

    fun onDeviceConnectionError(title: String?, content: String?)

    fun stopProgress()

    fun showLocationIntentDialog()

    fun askForPermissions()

    fun startScan()

    fun unregister()

    fun onDeviceConnectionSuccess()

}