package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.SerializedName

/**
 * Created by meeera on 30/6/17.
 */

data class UserSetting (

    /**
     * Gets session
     *
     * @return the session
     */
    @SerializedName("session")
    val session: Session? = null,

    /**
     * Gets settings
     *
     * @return the settings
     */
    @SerializedName("settings")
    val settings: Settings? = null
)
