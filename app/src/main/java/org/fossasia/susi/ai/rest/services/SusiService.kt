package org.fossasia.susi.ai.rest.services

import org.fossasia.susi.ai.rest.responses.susi.ChangeSettingResponse
import org.fossasia.susi.ai.rest.responses.susi.FiveStarSkillRatingResponse
import org.fossasia.susi.ai.rest.responses.susi.ForgotPasswordResponse
import org.fossasia.susi.ai.rest.responses.susi.GetRatingByUserResponse
import org.fossasia.susi.ai.rest.responses.susi.GetSkillFeedbackResponse
import org.fossasia.susi.ai.rest.responses.susi.ListGroupsResponse
import org.fossasia.susi.ai.rest.responses.susi.ListSkillMetricsResponse
import org.fossasia.susi.ai.rest.responses.susi.ListSkillsResponse
import org.fossasia.susi.ai.rest.responses.susi.LoginResponse
import org.fossasia.susi.ai.rest.responses.susi.MemoryResponse
import org.fossasia.susi.ai.rest.responses.susi.PostSkillFeedbackResponse
import org.fossasia.susi.ai.rest.responses.susi.ReportSkillResponse
import org.fossasia.susi.ai.rest.responses.susi.ResetPasswordResponse
import org.fossasia.susi.ai.rest.responses.susi.SignUpResponse
import org.fossasia.susi.ai.rest.responses.susi.SkillRatingResponse
import org.fossasia.susi.ai.rest.responses.susi.SusiResponse
import org.fossasia.susi.ai.rest.responses.susi.UserSetting

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap

/**
 * <h1>Retrofit service to get susi response.</h1>
 */
interface SusiService {

    /**
     * Gets chat history.
     *
     * @return the chat history
     */
    @get:GET("/susi/memory.json")
    val chatHistory: Call<MemoryResponse>

    /**
     * Get User settings
     *
     * @return settings
     */
    @get:GET("/aaa/listUserSettings.json")
    val userSetting: Call<UserSetting>

    /**
     * Gets susi response.
     *
     * @param query A query map consisting of the following key value pairs
     * timezoneOffset the timezone offset
     * longitude      the longitude
     * latitude       the latitude
     * geosource      the geosource
     * language       the language
     * query          the query
     * @return the susi response
     */
    @GET("/susi/chat.json")
    fun getSusiResponse(@QueryMap query: Map<String, String?>): Call<SusiResponse>

    /**
     * Sign up call.
     *
     * @param email the email
     * @param password the password
     * @return the call
     */
    @POST("/aaa/signup.json")
    fun signUp(
        @Query("signup") email: String,
        @Query("password") password: String
    ): Call<SignUpResponse>

    /**
     * Login call.
     *
     * @param email the email
     * @param password the password
     * @return the call
     */
    @POST("/aaa/login.json?type=access-token")
    fun login(
        @Query("login") email: String,
        @Query("password") password: String
    ): Call<LoginResponse>

    /**
     * Forgot password call.
     *
     * @param email the email
     * @return the call
     */
    @POST("/aaa/recoverpassword.json")
    fun forgotPassword(@Query("forgotemail") email: String): Call<ForgotPasswordResponse>

    /**
     * User settings call
     *
     * @param key
     * @param value
     * @return the call
     */
    @POST("/aaa/changeUserSettings.json")
    fun changeSettingResponse(
        @Query("key1") key: String,
        @Query("value1") value: String,
        @Query("count") count: Int
    ): Call<ChangeSettingResponse>

    /**
     * Send user rating to the server
     *
     * @param query A query map consisting of following key value pairs
     * model       Model of the skill (e.g. general)
     * group       Group of skill (e.g. Knowledge)
     * language    Language directory in which the skill resides (e.g. en)
     * skill       Skill Tag of object in which the skill data resides
     * rating      Rating either positive or negative
     * @return the Call
     */
    @POST("/cms/rateSkill.json")
    fun rateSkill(@QueryMap query: Map<String, String>): Call<SkillRatingResponse>

    /**
     * Send five star user rating to the server
     *
     * @param query A query map consisting of following key value pairs
     * model       Model of the skill (e.g. general)
     * group       Group of skill (e.g. Knowledge)
     * language    Language directory in which the skill resides (e.g. en)
     * skill       Skill Tag of object in which the skill data resides
     * stars      User rating to be sent
     * accessToken Access token of logged in user
     * @return the Call
     */
    @POST("/cms/fiveStarRateSkill.json")
    fun fiveStarRateSkill(@QueryMap query: Map<String, String>): Call<FiveStarSkillRatingResponse>

    /**
     * Reset Password call
     *
     * @param email
     * @param password
     * @param newPassword
     * @return the call
     */
    @POST("/aaa/changepassword.json")
    fun resetPasswordResponse(
        @Query("changepassword") email: String,
        @Query("password") password: String,
        @Query("newpassword") newPassword: String
    ): Call<ResetPasswordResponse>

    /**
     * Post feedback provided by user
     *
     * @param query A query map consisting of following key value pairs
     * model       Model of the skill (e.g. general)
     * group       Group of skill (e.g. Knowledge)
     * language    Language directory in which the skill resides (e.g. en)
     * skill       Skill Tag of object in which the skill data resides
     * feedback    User feedback to be posted
     * accessToken Access token of the logged in user
     * @return the Call
     */
    @POST("/cms/feedbackSkill.json")
    fun postFeedback(@QueryMap query: Map<String, String>): Call<PostSkillFeedbackResponse>

    /**
     * @return list of groups
     */
    @GET("/cms/getGroups.json")
    fun fetchListGroups(): Call<ListGroupsResponse>

    @GET("/cms/getSkillList.json")
    fun fetchListSkills(@QueryMap query: Map<String, String>): Call<ListSkillsResponse>

    @GET("/cms/getRatingByUser.json")
    fun getRatingByUser(@QueryMap query: Map<String, String>): Call<GetRatingByUserResponse>

    /**
     * Get feedback list from the server
     *
     * @param query A query map consisting of following key value pairs
     * model       Model of the skill (e.g. general)
     * group       Group of skill (e.g. Knowledge)
     * language    Language directory in which the skill resides (e.g. en)
     * skill       Skill Tag of object in which the skill data resides
     * @return the call
     */
    @GET("/cms/getSkillFeedback.json")
    fun fetchFeedback(@QueryMap query: Map<String, String>): Call<GetSkillFeedbackResponse>

    /**
     * Get skills based on different metrics from the server
     *
     * @param model Model of the skill (e.g. general)
     * @param language Language directory in which the skill resides (e.g. en)
     * @return the call
     */
    @GET("/cms/getSkillMetricsData.json")
    fun fetchSkillMetricsData(
        @Query("model") model: String,
        @Query("language") language: String
    ): Call<ListSkillMetricsResponse>

    /**
     * Report a skill
     * @param query A query  map consisting of the following key value pairs
     * model       Model of the skill(Eg. general)
     * group       Group of the skill(Eg. Knowledge)
     * skill       Skill to be reported
     * feedback    The report/msg send by the user
     * accessToken access token of the user
     * @return the call
     */
    @POST("cms/reportSkill.json")
    fun reportSkill(@QueryMap query: Map<String, String>): Call<ReportSkillResponse>
}
