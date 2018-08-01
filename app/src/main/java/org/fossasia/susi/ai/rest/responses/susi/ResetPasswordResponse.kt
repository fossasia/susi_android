package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by meeera on 4/8/17.
 */
data class ResetPasswordResponse (

    val session: Session? = null,

    val accepted: Boolean = false,

    val message: String? = null

)