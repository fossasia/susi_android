package org.fossasia.susi.ai.device.contract


interface IDeviceView {

    fun onDeviceConnectedSuccess()

    fun showProgress()

    fun onDeviceConnectionError(title: String?, content: String?)

    fun stopProgress()

    fun showLocationIntentDialog()

    fun askForPermissions()

    fun startScan()

}