package org.fossasia.susi.ai.device.contract


interface IDeviceConnectView {

    fun setupAdapter(devicesScanned: List<String>)

    fun showProgress(title: String?)

    fun onDeviceConnectionError(title: String?, content: String?)

    fun stopProgress()

    fun showLocationIntentDialog()

    fun askForPermissions()

    fun startScan()

    fun unregister()

    fun onDeviceConnectionSuccess()

}