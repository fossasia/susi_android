package org.fossasia.susi.ai.device.viewdevice

import org.fossasia.susi.ai.device.viewdevice.contract.IViewDevicePresenter
import org.fossasia.susi.ai.device.viewdevice.contract.IViewDeviceView
import org.fossasia.susi.ai.rest.responses.susi.Device

class ViewDevicePresenter : IViewDevicePresenter {

    private lateinit var viewDeviceView: IViewDeviceView
    private var macId: String? = null
    private var device: Device? = null

    override fun onAttach(viewDeviceView: IViewDeviceView, macId: String?, device: Device?) {
        this.viewDeviceView = viewDeviceView
        this.macId = macId
        this.device = device
    }
}