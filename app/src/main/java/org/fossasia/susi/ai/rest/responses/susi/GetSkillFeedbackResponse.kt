package org.fossasia.susi.ai.rest.responses.susi

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GetSkillFeedbackResponse(
    val session: Session? = null,
    val accepted: Boolean = false,
    val message: String = "",
    @SerializedName("feedback")
    var feedbackList: List<Feedback> = ArrayList(),
    @SerializedName("skill_name")
    var skillName: String = ""
) : Parcelable