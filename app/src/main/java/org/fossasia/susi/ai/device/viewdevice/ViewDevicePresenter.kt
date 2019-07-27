package org.fossasia.susi.ai.device.viewdevice

import io.realm.Realm
import org.fossasia.susi.ai.data.model.RoomsAvailable
import org.fossasia.susi.ai.device.viewdevice.contract.IViewDevicePresenter
import org.fossasia.susi.ai.device.viewdevice.contract.IViewDeviceView
import org.fossasia.susi.ai.rest.responses.susi.Device

class ViewDevicePresenter : IViewDevicePresenter {

    private lateinit var viewDeviceView: IViewDeviceView
    private var macId: String? = null
    private var device: Device? = null
    private lateinit var realm: Realm

    override fun onAttach(viewDeviceView: IViewDeviceView, macId: String?, device: Device?) {
        this.viewDeviceView = viewDeviceView
        this.macId = macId
        this.device = device
        realm = Realm.getDefaultInstance()
    }

    override fun addRoom(roomName: String) {
        realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        var results = realm.where(RoomsAvailable::class.java).findAll()
        var id = results.size
        id++

        var addedRoomModel = realm.createObject(RoomsAvailable::class.java)
        addedRoomModel.id = id.toLong()
        addedRoomModel.room = roomName
        realm.commitTransaction()
        viewDeviceView.showRooms()
        // deviceConnectView?.roomNameSelected(null)
    }

    override fun deleteRoom(room: String?) {
        realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val result = realm.where(RoomsAvailable::class.java).equalTo("room", room).findFirst()
        result?.deleteFromRealm()
        realm.commitTransaction()
        viewDeviceView.showRooms()
    }

    override fun selectedRoom(roomName: String?) {
        // Selected room callback
        viewDeviceView.roomNameSelected(roomName)
    }
}