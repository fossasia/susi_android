package org.fossasia.susi.ai.device

import org.fossasia.susi.ai.device.contract.IDevicePresenter
import org.fossasia.susi.ai.device.contract.IDeviceView

class DevicePresenter : IDevicePresenter {

    var deviceView: IDeviceView? = null

    override fun onAttach(deviceView: IDeviceView) {
        this.deviceView = deviceView
    }

    override fun searchDevices() {
    }

    override fun onDetach() {
        this.deviceView = null
    }
}
