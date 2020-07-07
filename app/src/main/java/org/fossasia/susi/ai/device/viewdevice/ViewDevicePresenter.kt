package org.fossasia.susi.ai.device.viewdevice

import android.content.Context
import io.realm.Realm
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.data.UtilModel
import org.fossasia.susi.ai.data.contract.IRoomModel
import org.fossasia.susi.ai.data.device.RoomModel
import org.fossasia.susi.ai.data.model.RoomsAvailable
import org.fossasia.susi.ai.dataclasses.AddDeviceQuery
import org.fossasia.susi.ai.device.viewdevice.contract.IViewDevicePresenter
import org.fossasia.susi.ai.device.viewdevice.contract.IViewDeviceView
import org.fossasia.susi.ai.rest.responses.susi.Device
import org.fossasia.susi.ai.rest.responses.susi.GetAddDeviceResponse
import retrofit2.Response
import timber.log.Timber

class ViewDevicePresenter(private val viewDeviceView: IViewDeviceView) : IViewDevicePresenter, IRoomModel.onAddDeviceListener {

    private var macId: String? = null
    private var device: Device? = null
    private lateinit var realm: Realm
    private val roomModel: RoomModel = RoomModel()
    private lateinit var context: Context
    private lateinit var utilModel: UtilModel

    override fun onAttach(viewDeviceView: IViewDeviceView, macId: String?, device: Device?, context: Context) {
        this.macId = macId
        this.device = device
        this.context = context
        realm = Realm.getDefaultInstance()
        utilModel = UtilModel(context)
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

    override fun updateDevice(queryObject: AddDeviceQuery) {
        // Updating the device to the server
        roomModel.addDeviceToServer(queryObject, this)
    }

    override fun onAddDeviceSuccess(response: Response<GetAddDeviceResponse>) {
        Timber.d("Successfully updated the device information")
        viewDeviceView.viewDetails()
        viewDeviceView.stopProgressbar()
        viewDeviceView.showToast(utilModel.getString(R.string.succesfully_updated_device))
    }

    override fun onError(throwable: Throwable) {
        Timber.d("SFailed to update the device information " + throwable)
        viewDeviceView.showToast(utilModel.getString(R.string.failed_device_update))
    }
}
