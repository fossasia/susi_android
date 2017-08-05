package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by meeera on 4/8/17.
 */
class ResetPasswordResponse {

    @SerializedName("session")
    @Expose
    val session: Session? = null

    @SerializedName("accepted")
    @Expose
    val accepted: Boolean = false

    @SerializedName("message")
    @Expose
    val message: String? = null

}