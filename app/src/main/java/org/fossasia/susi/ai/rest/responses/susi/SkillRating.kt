package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 *
 * Created by chiragw15 on 18/8/17.
 */
class SkillRating: Serializable {
    @SerializedName("positive")
    @Expose
    var positive: Int = 0

    @SerializedName("negative")
    @Expose
    var negative: Int = 0
}