package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TableAction {

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("columns")
    @Expose
    var columns: Map<String, Any>? = null

    @SerializedName("count")
    @Expose
    var count: Long = 0;
}