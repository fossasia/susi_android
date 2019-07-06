package org.fossasia.susi.ai.device.deviceconnect.contract

interface IDeviceConnectView {

    fun setupDeviceAdapter(scanList: List<String>)

    fun showProgress(title: String?)

    fun onDeviceConnectionError(title: String?, content: String?)

    fun stopProgress()

    fun showLocationIntentDialog()

    fun showWifiIntentDialog()

    fun askForPermissions()

    fun startScan(isDevice: Boolean)

    fun unregister()

    fun onDeviceConnectionSuccess(message: String)

    fun setupWiFiAdapter(scanList: ArrayList<String>)

    fun showPopUpDialog()

    fun rooms()

    fun showRooms()

    fun addDeviceProcess()

    fun addDevice(latitude: String, longitude: String)

    fun connectionMainScreen()

    fun showDialog(message: String, title: String)

    fun wifiSetup()

    fun roomNameSelected(roomName: String?)
}