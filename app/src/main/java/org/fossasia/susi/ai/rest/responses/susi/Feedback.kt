package org.fossasia.susi.ai.rest.responses.susi

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Feedback(
    var feedback: String? = "",
    var email: String? = "",
    var timestamp: String? = "",
    @SerializedName("user_name")
    var userName: String = "",
    var avatar: String? = ""
) : Parcelable