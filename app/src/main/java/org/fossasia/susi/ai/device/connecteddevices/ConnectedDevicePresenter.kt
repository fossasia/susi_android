package org.fossasia.susi.ai.device.connecteddevices

import org.fossasia.susi.ai.data.contract.IRoomModel
import org.fossasia.susi.ai.data.device.RoomModel
import org.fossasia.susi.ai.device.connecteddevices.contract.IConnectedDevicePresenter
import org.fossasia.susi.ai.device.connecteddevices.contract.IConnectedDeviceView
import org.fossasia.susi.ai.rest.responses.susi.ConnectedDevicesResponse
import retrofit2.Response
import timber.log.Timber

class ConnectedDevicePresenter(val connectedDeviceFragment: ConnectedDeviceFragment) :
        IConnectedDevicePresenter,
        IRoomModel.onConnectedDeviceFetchingFinishedListener {

    private val roomModel: RoomModel = RoomModel()
    private var connectedDeviceView: IConnectedDeviceView? = null

    override fun onAttach(connectedDeviceView: IConnectedDeviceView) {
        this.connectedDeviceView = connectedDeviceView
    }

    override fun getDevices() {
        roomModel.getConnectedDevices(this)
    }

    override fun onRoomModelSuccess(response: Response<ConnectedDevicesResponse>) {
        Timber.d("Showing list of connected devices")
        val deviceResponse = response.body()
        val deviceResponseMap = deviceResponse?.devices
        connectedDeviceView?.getConnectedDeviceDetails(deviceResponseMap)
    }

    override fun onError(throwable: Throwable) {
        Timber.d("Failed to fetch data about list of available devices")
        Timber.d(throwable)
    }

    override fun openViewDevice(position: Int) {
        connectedDeviceView?.viewDevice(position)
    }
}
