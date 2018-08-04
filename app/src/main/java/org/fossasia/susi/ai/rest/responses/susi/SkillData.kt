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
    @Expose
    val model: String = ""

    @SerializedName("group")
    @Expose
    val group: String = ""

    @SerializedName("language")
    @Expose
    val language: String = ""

    @SerializedName("image")
    @Expose
    var image: String = ""

    @SerializedName("author_url")
    @Expose
    var authorUrl: String = ""

    @SerializedName("examples")
    @Expose
    var examples: List<String> = ArrayList()

    @SerializedName("developer_privacy_policy")
    @Expose
    var developerPrivacyPolicy: String = ""

    @SerializedName("author")
    @Expose
    var author: String = ""

    @SerializedName("skill_name")
    @Expose
    var skillName: String = ""

    @SerializedName("dynamic_content")
    @Expose
    var dynamicContent: Boolean? = null

    @SerializedName("terms_of_use")
    @Expose
    var termsOfUse: String = ""

    @SerializedName("descriptions")
    @Expose
    var descriptions: String = ""

    @SerializedName("skill_rating")
    @Expose
    var skillRating: SkillRating? = null

    @SerializedName("skill_tag")
    @Expose
    var skillTag: String = ""

}