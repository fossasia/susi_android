package org.fossasia.susi.ai.rest.responses.susi

/**
 *
 * Created by chiragw15 on 16/8/17.
 */
class SkillData (

    var image: String = "",
    var authorUrl: String = "",
    var examples: List<String> = ArrayList(),
    var developerPrivacyPolicy: String ?= null,
    var author: String = "",
    var skillName: String = "",
    var dynamicContent: Boolean = false,

    var termsOfUse: String ?= null,
    var descriptions: String = "",
    var skillRating: SkillRating ?= null

)