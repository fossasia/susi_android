package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose

/**
 *
 * Created by chiragw15 on 16/8/17.
 */
class ListSkillsResponse {


    @Expose
    val group: String = "Knowledge"


    @Expose
    val skillMap: Map<String, SkillData> = HashMap()
}