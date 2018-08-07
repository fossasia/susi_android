package org.fossasia.susi.ai.rest.responses.susi

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 *
 * Created by chiragw15 on 16/8/17.
 */
@Parcelize
class SkillData : Parcelable {

    @SerializedName("model")
    val model: String = ""
    @SerializedName("group")
    val group: String = ""
    @SerializedName("language")
    val language: String = ""
    @SerializedName("image")
    var image: String = ""
    @SerializedName("author_url")
    var authorUrl: String = ""
    @SerializedName("examples")
    var examples: List<String> = ArrayList()
    @SerializedName("developer_privacy_policy")
    var developerPrivacyPolicy: String = ""
    @SerializedName("author")
    var author: String = ""
    @SerializedName("skill_name")
    var skillName: String = ""
    @SerializedName("dynamic_content")
    var dynamicContent: Boolean? = null
    @SerializedName("terms_of_use")
    var termsOfUse: String = ""
    @SerializedName("descriptions")
    var descriptions: String = ""
    @SerializedName("skill_rating")
    var skillRating: SkillRating? = null
    @SerializedName("skill_tag")
    var skillTag: String = ""
}