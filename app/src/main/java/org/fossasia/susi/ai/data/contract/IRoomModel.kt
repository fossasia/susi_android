package org.fossasia.susi.ai.data.contract

import org.fossasia.susi.ai.dataclasses.AddDeviceQuery

interface IRoomModel {
    fun addDeviceToServer(query: AddDeviceQuery)
    fun getConnectedDevices()
}