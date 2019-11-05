package org.fossasia.susi.ai.data.model

import io.realm.RealmObject

open class RoomsAvailable(
    var id: Long = 0,
    var room: String? = null
) : RealmObject()
