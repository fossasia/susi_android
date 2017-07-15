package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * <h1>Kotlin Data class to parse retrofit response from memory endpoint susi client.</h1>

 * Created by chiragw15 on 25/5/17.
 */

class MemoryResponse {

    @SerializedName("cognitions")
    @Expose
    var cognitionsList: List<SusiResponse>? = null

    @SerializedName("session")
    @Expose
    var session: Session? = null

}
