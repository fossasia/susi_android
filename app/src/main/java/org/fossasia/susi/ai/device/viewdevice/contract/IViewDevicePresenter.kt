package org.fossasia.susi.ai.device.viewdevice.contract

import android.content.Context
import org.fossasia.susi.ai.dataclasses.AddDeviceQuery
import org.fossasia.susi.ai.rest.responses.susi.Device

interface IViewDevicePresenter {

    fun onAttach(viewDeviceView: IViewDeviceView, macId: String?, device: Device?, context: Context)

    fun addRoom(roomName: String)

    fun deleteRoom(room: String?)

    fun selectedRoom(roomName: String?)

    fun updateDevice(queryObject: AddDeviceQuery)
}
