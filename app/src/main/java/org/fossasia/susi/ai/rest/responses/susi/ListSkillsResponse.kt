package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 *
 * Created by chiragw15 on 16/8/17.
 */
class ListSkillsResponse {

    @SerializedName("group")
    @Expose
    val group: String = "Knowledge"

    @SerializedName("skills")
    @Expose
    val skillMap: Map<String, SkillData> = HashMap()
}