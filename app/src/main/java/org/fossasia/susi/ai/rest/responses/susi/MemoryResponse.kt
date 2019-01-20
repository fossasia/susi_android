package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.SerializedName

data class MemoryResponse(
    @SerializedName("cognitions")
    var cognitionsList: List<SusiResponse> = ArrayList(),
    var session: Session? = null
)
