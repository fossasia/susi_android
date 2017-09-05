package org.fossasia.susi.ai.rest.responses.susi

/**
 *
 * Created by chiragw15 on 16/8/17.
 */
class ListSkillsResponse (

    val group: String = "Knowledge",
    val skillMap: Map<String, SkillData> = HashMap()
    )