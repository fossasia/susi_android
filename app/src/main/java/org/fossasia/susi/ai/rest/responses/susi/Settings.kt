package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose

/**
 * Created by meeera on 30/6/17.
 */

class Settings {


    @Expose
    val speechAlways: Boolean = false


    @Expose
    val enterSend: Boolean = false


    @Expose
    val speechOutput: Boolean = true


    @Expose
    val micInput: Boolean = true


    @Expose
    val language: String = "default"

}
