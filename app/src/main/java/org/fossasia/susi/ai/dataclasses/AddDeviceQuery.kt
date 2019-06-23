package org.fossasia.susi.ai.dataclasses

data class AddDeviceQuery(
    val accessToken: String,
    val macId: String,
    val name: String,
    val room: String,
    val latitude: String,
    val longitude: String
)