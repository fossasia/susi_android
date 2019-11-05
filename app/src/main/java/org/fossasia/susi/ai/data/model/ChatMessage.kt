package org.fossasia.susi.ai.data.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.fossasia.susi.ai.rest.responses.susi.Datum

open class ChatMessage(
    @PrimaryKey
    var id: Long = 0,
    var content: String? = null,
    var timeStamp: String? = null,
    var date: String? = null,
    var webquery: String? = null,
    var actionType: String? = null,
    var skillLocation: String? = null,
    var datumRealmList: RealmList<Datum>? = RealmList(),
    var webLinkData: WebLink? = null,
    var webSearchList: RealmList<WebSearchModel>? = null,
    var tableColumns: RealmList<TableColumn>? = null,
    var tableData: RealmList<TableData>? = null,
    var isDelivered: Boolean = false,
    var isHavingLink: Boolean = false,
    var isDate: Boolean = false,
    var isMine: Boolean = false,
    var isPositiveRated: Boolean = false,
    var isNegativeRated: Boolean = false,
    var latitude: Double = 0.toDouble(),
    var longitude: Double = 0.toDouble(),
    var zoom: Double = 0.toDouble(),
    var identifier: String? = null
) : RealmObject()
