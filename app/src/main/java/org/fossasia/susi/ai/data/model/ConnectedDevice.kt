package org.fossasia.susi.ai.data.model

import io.realm.RealmObject

open class ConnectedDevice(
    var id: Long = 0,
    var ssid: String? = null,
    var datetime: String? = null
) : RealmObject()