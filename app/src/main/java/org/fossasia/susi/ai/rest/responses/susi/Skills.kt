package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 *
 * Created by cc15 on 16/8/17.
 */
class Skills {
    @SerializedName("aboutsusi")
    @Expose
    val skillData: SkillData ?= null
}