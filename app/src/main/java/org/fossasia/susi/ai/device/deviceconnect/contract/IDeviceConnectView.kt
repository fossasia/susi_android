package org.fossasia.susi.ai.device.deviceconnect.contract

interface IDeviceConnectView {

    fun setupDeviceAdapter(scanList: List<String>)

    fun showProgress(title: String?)

    fun onDeviceConnectionError(title: String?, content: String?)

    fun stopProgress()

    fun askForPermissions()

    fun startScan(isDevice: Boolean)

    fun unregister()

    fun onDeviceConnectionSuccess(message: String)

    fun setupWiFiAdapter(scanList: ArrayList<String>)

    fun rooms()

    fun showRooms()

    fun connectionMainScreen()

    fun showDialog(message: String, title: String)

    fun wifiSetup()

    fun roomNameSelected(roomName: String?)

    fun showToast(message: String)

    fun passwordLayoutSetup()

    fun showAnonymousDialog()

    fun finishSetup()

    fun successSetup()

    fun selectedWifi(wifiName: String)
}
