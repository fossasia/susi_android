package org.fossasia.susi.ai.device.contract

interface IDeviceView {

    fun onDeviceConnectedSuccess()

    fun showProgress()

    fun onDeviceConnectionError()

}