package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 *
 * Created by chiragw15 on 16/8/17.
 */
class SkillData {

    @SerializedName("image")
    @Expose
    val image: String = ""

    @SerializedName("author_url")
    @Expose
    val authorUrl: String = ""

    @SerializedName("examples")
    @Expose
    val examples: List<String> = ArrayList()

    @SerializedName("developer_privacy_policy")
    @Expose
    val developerPrivacyPolicy: String ?= null

    @SerializedName("author")
    @Expose
    val author: String = ""

    @SerializedName("skill_name")
    @Expose
    val skillName: String = ""

    @SerializedName("dynamic_content")
    @Expose
    val dynamicContent: Boolean = false

    @SerializedName("terms_of_use")
    @Expose
    val termsOfUse: String ?= null

    @SerializedName("descriptions")
    @Expose
    val descriptions: String = ""

    @SerializedName("skill_rating")
    @Expose
    val skillRating: Int ?= null
}