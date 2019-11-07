package org.fossasia.susi.ai.device.connecteddevices.contract

interface IConnectedDevicePresenter {

    fun onAttach(connectedDeviceView: IConnectedDeviceView)

    fun getDevices()

    fun openViewDevice(position: Int)
}
