package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import java.util.ArrayList

class TableAnswer {

    @SerializedName("data")
    @Expose
    var data: List<Map<String, Any>>? = null

    @SerializedName("metadata")
    @Expose
    val metadata: Metadata? = null

    @SerializedName("actions")
    @Expose
    val actions: List<TableAction> = ArrayList()

    @SerializedName("skills")
    @Expose
    val skills: List<String> = ArrayList()
}