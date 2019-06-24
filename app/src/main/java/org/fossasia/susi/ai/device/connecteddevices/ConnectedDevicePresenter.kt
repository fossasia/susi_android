package org.fossasia.susi.ai.device.connecteddevices

import android.util.Log
import org.fossasia.susi.ai.data.contract.IRoomModel
import org.fossasia.susi.ai.data.device.RoomModel
import org.fossasia.susi.ai.device.connecteddevices.contract.IConnectedDevicePresenter
import org.fossasia.susi.ai.rest.responses.susi.ConnectedDevicesResponse
import org.fossasia.susi.ai.rest.responses.susi.Device
import retrofit2.Response
import timber.log.Timber

class ConnectedDevicePresenter(val connectedDeviceFragment: ConnectedDeviceFragment) :
        IConnectedDevicePresenter,
        IRoomModel.onConnectedDeviceFetchingFinishedListener {

    private var connectedDevicesList: ArrayList<Device> = ArrayList()
    private var roomModel: RoomModel = RoomModel()

    override fun getDevices() {
        roomModel.getConnectedDevices(this)
    }

    override fun onRoomModelSuccess(response: Response<ConnectedDevicesResponse>) {
        Log.d("KHANKI", "Success " + response)
        val deviceResponse = response.body()
        val deviceResponseMap = deviceResponse?.devices
        Log.d("KHANKI", "MAP" + deviceResponseMap.toString())

//        deviceResponseMap?.device?.forEach { device ->
//            Log.d("KHANKI", " Name - "+ device.name)
//            connectedDevicesList.add(device)
//        }
// //
//        Timber.d("KHANKI" + connectedDevicesList)
    }

    override fun onError(throwable: Throwable) {
        Timber.d(throwable)
    }
}