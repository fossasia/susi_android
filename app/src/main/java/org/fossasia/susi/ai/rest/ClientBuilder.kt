package org.fossasia.susi.ai.rest

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.fossasia.susi.ai.dataclasses.ReportSkillQuery
import org.fossasia.susi.ai.dataclasses.SkillRatingQuery
import org.fossasia.susi.ai.dataclasses.SkillsListQuery
import org.fossasia.susi.ai.dataclasses.FetchFeedbackQuery
import org.fossasia.susi.ai.helper.PrefManager
import org.fossasia.susi.ai.rest.interceptors.TokenInterceptor
import org.fossasia.susi.ai.rest.responses.susi.ListSkillsResponse
import org.fossasia.susi.ai.rest.responses.susi.ReportSkillResponse
import org.fossasia.susi.ai.rest.responses.susi.SkillRatingResponse
import org.fossasia.susi.ai.rest.responses.susi.GetSkillFeedbackResponse
import org.fossasia.susi.ai.rest.services.SusiService

import java.util.HashMap

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ClientBuilder {

    val retrofit: Retrofit by lazy {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()
        //Must maintain the order of interceptors here, logging needs to be last.
        httpClient.addInterceptor(TokenInterceptor())
        httpClient.addInterceptor(logging)

        Retrofit.Builder()
                .baseUrl(PrefManager.susiRunningBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build()
    }

    val susiApi: SusiService by lazy {
        retrofit.create(SusiService::class.java)
    }

    fun fetchFeedbackCall(queryObject: FetchFeedbackQuery): Call<GetSkillFeedbackResponse> {
        val queryMap = HashMap<String, String>()
        queryMap["model"] = queryObject.model
        queryMap["group"] = queryObject.group
        queryMap["language"] = queryObject.language
        queryMap["skill"] = queryObject.skill
        return susiApi.fetchFeedback(queryMap)
    }

    fun rateSkillCall(queryObject: SkillRatingQuery): Call<SkillRatingResponse> {
        val queryMap = HashMap<String, String>()
        queryMap["model"] = queryObject.model
        queryMap["group"] = queryObject.group
        queryMap["language"] = queryObject.language
        queryMap["skill"] = queryObject.skill
        queryMap["rating"] = queryObject.rating
        return susiApi.rateSkill(queryMap)
    }

    fun fetchListSkillsCall(queryObject: SkillsListQuery): Call<ListSkillsResponse> {
        val queryMap = HashMap<String, String>()
        queryMap["group"] = queryObject.group
        queryMap["language"] = queryObject.language
        queryMap["applyFilter"] = queryObject.applyFilter
        queryMap["filter_name"] = queryObject.filterName
        queryMap["filter_type"] = queryObject.filterType
        return susiApi.fetchListSkills(queryMap)
    }

    fun sendReportCall(queryObject: ReportSkillQuery): Call<ReportSkillResponse> {
        val map = HashMap<String, String>()
        map.put("access_token", queryObject.accessToken)
        map.put("feedback", queryObject.feedback)
        map.put("skill", queryObject.skill)
        map.put("group", queryObject.group)
        map.put("model", queryObject.model)
        return susiApi.reportSkill(map)
    }
}