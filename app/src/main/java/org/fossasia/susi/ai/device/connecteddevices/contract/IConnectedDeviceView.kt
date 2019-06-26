package org.fossasia.susi.ai.device.connecteddevices.contract

import org.fossasia.susi.ai.rest.responses.susi.Device

interface IConnectedDeviceView {

    fun getConnectedDeviceDetails(deviceResponseMap: Map<String, Device>?)
}