package org.fossasia.susi.ai.helper

import org.fossasia.susi.ai.dataclasses.SkillRatingQuery
import org.fossasia.susi.ai.dataclasses.SkillsListQuery
import org.fossasia.susi.ai.rest.ClientBuilder
import org.fossasia.susi.ai.rest.responses.susi.ListSkillsResponse
import org.fossasia.susi.ai.rest.responses.susi.SkillRatingResponse
import retrofit2.Call

class QueryHelper {
    fun rateSkillCall(queryObject: SkillRatingQuery): Call<SkillRatingResponse> {
        val queryMap: MutableMap<String, String> = HashMap()
        queryMap.put("model", queryObject.model)
        queryMap.put("group", queryObject.group)
        queryMap.put("language", queryObject.language)
        queryMap.put("skill", queryObject.skill)
        queryMap.put("rating", queryObject.rating)
        val call: Call<SkillRatingResponse> = ClientBuilder().getSusiApi().rateSkill(queryMap)
        return call
    }

    fun fetchListSkillsCall(queryObject: SkillsListQuery): Call<ListSkillsResponse> {
        val queryMap: MutableMap<String, String> = HashMap()
        queryMap.put("group", queryObject.group)
        queryMap.put("language", queryObject.language)
        queryMap.put("applyFilter", queryObject.applyFilter)
        queryMap.put("filter_name", queryObject.filterName)
        queryMap.put("filter_type", queryObject.filterType)
        val call: Call<ListSkillsResponse> = ClientBuilder().getSusiApi().fetchListSkills(queryMap)
        return call
    }
}