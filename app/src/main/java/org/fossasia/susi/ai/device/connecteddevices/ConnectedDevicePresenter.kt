package org.fossasia.susi.ai.device.connecteddevices

import org.fossasia.susi.ai.data.device.RoomModel
import org.fossasia.susi.ai.device.connecteddevices.contract.IConnectedDevicePresenter

class ConnectedDevicePresenter(val connectedDeviceFragment: ConnectedDeviceFragment) : IConnectedDevicePresenter {

    private var roomModel: RoomModel = RoomModel()

    override fun getDevices() {
        roomModel.getConnectedDevices()
    }
}