package org.fossasia.susi.ai.rest.responses.susi

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SkillData(
    val model: String = "",
    val group: String = "",
    val language: String = "",
    var image: String = "",
    @SerializedName("author_url")
    var authorUrl: String = "",
    var examples: List<String> = ArrayList(),
    @SerializedName("developer_privacy_policy")
    var developerPrivacyPolicy: String = "",
    var author: String = "",
    @SerializedName("skill_name")
    var skillName: String = "",
    @SerializedName("dynamic_content")
    var dynamicContent: Boolean? = null,
    @SerializedName("terms_of_use")
    var termsOfUse: String = "",
    var descriptions: String = "",
    @SerializedName("skill_rating")
    var skillRating: SkillRating? = null,
    @SerializedName("skill_tag")
    var skillTag: String = ""
) : Parcelable
