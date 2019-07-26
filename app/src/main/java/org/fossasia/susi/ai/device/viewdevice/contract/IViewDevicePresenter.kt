package org.fossasia.susi.ai.device.viewdevice.contract

import org.fossasia.susi.ai.rest.responses.susi.Device

interface IViewDevicePresenter {

    fun onAttach(viewDeviceView: IViewDeviceView, macId: String?, device: Device?)

    fun addRoom(roomName: String)
}