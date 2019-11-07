package org.fossasia.susi.ai.device.viewdevice.contract

interface IViewDeviceView {

    fun viewDetails()

    fun initialSetUp()

    fun changeSpeakerName()

    fun changeRoomName()

    fun showRooms()

    fun roomNameSelected(roomName: String?)

    fun addDevice()

    fun showProgressbar()

    fun stopProgressbar()

    fun showToast(message: String)
}
