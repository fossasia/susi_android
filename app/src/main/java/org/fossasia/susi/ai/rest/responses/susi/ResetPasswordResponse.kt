package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose

/**
 * Created by meeera on 4/8/17.
 */
class ResetPasswordResponse {


    @Expose
    val session: Session? = null


    @Expose
    val accepted: Boolean = false


    @Expose
    val message: String? = null

}