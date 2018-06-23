package org.fossasia.susi.ai.device.contract


interface IDeviceView {

    fun onDeviceConnectedSuccess(devicesScanned: List<String>)

    fun showProgress()

    fun onDeviceConnectionError(title: String?, content: String?)

    fun stopProgress()

    fun showLocationIntentDialog()

    fun askForPermissions()

    fun startScan()

    fun unregister()

}