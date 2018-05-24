package org.fossasia.susi.ai.device.contract

interface IDevicePresenter {

    fun onAttach(deviceView: IDeviceView)

    fun searchDevices()

    fun onDetach()
}