package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by meeera on 30/6/17.
 */

class Settings {

    @SerializedName("speechOutputAlways")
    @Expose
    val speechAlways: Boolean = false

    @SerializedName("enterAsSend")
    @Expose
    val enterSend: Boolean = false

    @SerializedName("speechOutput")
    @Expose
    val speechOutput: Boolean = true

    @SerializedName("micInput")
    @Expose
    val micInput: Boolean = true

    @SerializedName("prefLanguage")
    @Expose
    val language: String = "default"

}
