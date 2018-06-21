package org.fossasia.susi.ai.device.contract


interface IDeviceView {

    fun onDeviceConnectedSuccess()

    fun showProgress()

    fun onDeviceConnectionError(title: Int, content: Int)

    fun stopProgress()

    fun showLocationIntentDialog()

    fun askForPermissions()

    fun startScan()

}