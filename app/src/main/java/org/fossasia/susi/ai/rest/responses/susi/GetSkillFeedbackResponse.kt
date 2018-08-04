package org.fossasia.susi.ai.rest.responses.susi

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 *
 * Created by arundhati24 on 27/06/2018
 */
@Parcelize
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
) : Parcelable