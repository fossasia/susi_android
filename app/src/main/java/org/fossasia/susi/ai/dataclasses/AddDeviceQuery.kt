package org.fossasia.susi.ai.dataclasses

data class AddDeviceQuery(
    val access_token: String,
    val macid: String,
    val name: String,
    val room: String,
    val latitude: String,
    val longitude: String
)