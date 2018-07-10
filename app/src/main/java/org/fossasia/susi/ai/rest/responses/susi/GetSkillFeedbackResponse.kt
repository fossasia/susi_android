package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 *
 * Created by arundhati24 on 27/06/2018
 */
data class GetSkillFeedbackResponse(
        val session: Session? = null,
        val accepted: Boolean = false,
        val message: String = "",

        @SerializedName("feedback")
        @Expose
        var feedbackList: List<Feedback> = ArrayList(),

        @SerializedName("skill_name")
        @Expose
        var skillName: String = ""
) : Serializable