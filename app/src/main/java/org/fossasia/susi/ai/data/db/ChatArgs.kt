package org.fossasia.susi.ai.data.db

import org.fossasia.susi.ai.data.model.MapData
import org.fossasia.susi.ai.data.model.TableItem
import org.fossasia.susi.ai.rest.responses.susi.Datum

data class ChatArgs(
    val prevId: Long = 0L,
    val message: String = "",
    val isDate: Boolean = false,
    val date: String = "",
    val timeStamp: String = "",
    val mine: Boolean = false,
    val actionType: String = "",
    val mapData: MapData? = null,
    val isHavingLink: Boolean = false,
    val datumList: List<Datum>? = null,
    val webSearch: String = "",
    val identifier: String? = "",
    val tableItem: TableItem? = null,
    val skillLocation: String = ""
)
