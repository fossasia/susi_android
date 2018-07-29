package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 *
 * Created by chiragw15 on 18/8/17.
 */
data class SkillRating (

    var positive: Int = 0,

    var negative: Int = 0,

    var stars: Stars? = null
)