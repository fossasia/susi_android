package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by meeera on 30/6/17.
 */

class ChangeSettingResponse {

    /**
     * Gets session
     *
     * @return the session
     */
    @SerializedName("session")
    @Expose
    val session: Session? = null

    /**
     * Gets message
     *
     * @return the message
     */
    @SerializedName("message")
    @Expose
    val message: String? = null
}
