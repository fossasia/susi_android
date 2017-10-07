package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 *
 * Created by chiragw15 on 16/8/17.
 */
class SkillData: Serializable {


    @Expose
    var image: String = ""


    @Expose
    var authorUrl: String = ""


    @Expose
    var examples: List<String> = ArrayList()


    @Expose
    var developerPrivacyPolicy: String = ""


    @Expose
    var author: String = ""


    @Expose
    var skillName: String = ""


    @Expose
    var dynamicContent: Boolean ?= null


    @Expose
    var termsOfUse: String = ""


    @Expose
    var descriptions: String = ""


    @Expose
    var skillRating: SkillRating ?= null
}