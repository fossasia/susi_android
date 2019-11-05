package org.fossasia.susi.ai.rest.responses.susi

data class ConnectedDevicesResponse(
    val devices: Map<String, Device>? = null
)
