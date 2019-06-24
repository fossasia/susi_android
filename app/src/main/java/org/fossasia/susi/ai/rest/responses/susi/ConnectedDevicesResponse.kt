package org.fossasia.susi.ai.rest.responses.susi

data class ConnectedDevicesResponse(
    val session: Session? = null,
    val settings: Settings? = null,
    val devices: Devices? = null,
    val lastLoginIP: String? = null,
    val lastLoginTime: String? = null
)