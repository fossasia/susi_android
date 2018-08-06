package org.fossasia.susi.ai.rest.services;

import org.fossasia.susi.ai.rest.clients.BaseUrl;
import org.fossasia.susi.ai.rest.responses.susi.ChangeSettingResponse;
import org.fossasia.susi.ai.rest.responses.susi.FiveStarSkillRatingResponse;
import org.fossasia.susi.ai.rest.responses.susi.ForgotPasswordResponse;
import org.fossasia.susi.ai.rest.responses.susi.GetRatingByUserResponse;
import org.fossasia.susi.ai.rest.responses.susi.GetSkillFeedbackResponse;
import org.fossasia.susi.ai.rest.responses.susi.ListGroupsResponse;
import org.fossasia.susi.ai.rest.responses.susi.ListSkillMetricsResponse;
import org.fossasia.susi.ai.rest.responses.susi.ListSkillsResponse;
import org.fossasia.susi.ai.rest.responses.susi.LoginResponse;
import org.fossasia.susi.ai.rest.responses.susi.MemoryResponse;
import org.fossasia.susi.ai.rest.responses.susi.PostSkillFeedbackResponse;
import org.fossasia.susi.ai.rest.responses.susi.ReportSkillResponse;
import org.fossasia.susi.ai.rest.responses.susi.ResetPasswordResponse;
import org.fossasia.susi.ai.rest.responses.susi.SignUpResponse;
import org.fossasia.susi.ai.rest.responses.susi.SkillRatingResponse;
import org.fossasia.susi.ai.rest.responses.susi.SusiBaseUrls;
import org.fossasia.susi.ai.rest.responses.susi.SusiResponse;
import org.fossasia.susi.ai.rest.responses.susi.UserSetting;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * <h1>Retrofit service to get susi response.</h1>
 */
public interface SusiService {

    /**
     * Gets susi response.
     *
     * @param query A query map consisting of the following key value pairs
     *              timezoneOffset the timezone offset
     *              longitude      the longitude
     *              latitude       the latitude
     *              geosource      the geosource
     *              language       the language
     *              query          the query
     * @return the susi response
     */
    @GET("/susi/chat.json")
    Call<SusiResponse> getSusiResponse(@QueryMap Map<String, String> query);

    /**
     * Gets chat history.
     *
     * @return the chat history
     */
    @GET("/susi/memory.json")
    Call<MemoryResponse> getChatHistory();

    /**
     * Sign up call.
     *
     * @param email    the email
     * @param password the password
     * @return the call
     */
    @POST("/aaa/signup.json")
    Call<SignUpResponse> signUp(@Query("signup") String email,
                                @Query("password") String password);

    /**
     * Login call.
     *
     * @param email    the email
     * @param password the password
     * @return the call
     */
    @POST("/aaa/login.json?type=access-token")
    Call<LoginResponse> login(@Query("login") String email,
                              @Query("password") String password);

    /**
     * Forgot password call.
     *
     * @param email the email
     * @return the call
     */
    @POST("/aaa/recoverpassword.json")
    Call<ForgotPasswordResponse> forgotPassword(@Query("forgotemail") String email);

    /**
     * User settings call
     *
     * @param key
     * @param value
     * @return the call
     */
    @POST("/aaa/changeUserSettings.json")
    Call<ChangeSettingResponse> changeSettingResponse(@Query("key1") String key,
                                                      @Query("value1") String value,
                                                      @Query("count") int count);

    /**
     * Get User settings
     *
     * @return settings
     */
    @GET("/aaa/listUserSettings.json")
    Call<UserSetting> getUserSetting();

    /**
     * Send user rating to the server
     *
     * @param query A query map consisting of following key value pairs
     *              model       Model of the skill (e.g. general)
     *              group       Group of skill (e.g. Knowledge)
     *              language    Language directory in which the skill resides (e.g. en)
     *              skill       Skill Tag of object in which the skill data resides
     *              rating      Rating either positive or negative
     * @return the Call
     */
    @POST("/cms/rateSkill.json")
    Call<SkillRatingResponse> rateSkill(@QueryMap Map<String, String> query);

    /**
     * Send five star user rating to the server
     *
     * @param query A query map consisting of following key value pairs
     *              model       Model of the skill (e.g. general)
     *              group       Group of skill (e.g. Knowledge)
     *              language    Language directory in which the skill resides (e.g. en)
     *              skill       Skill Tag of object in which the skill data resides
     *              stars      User rating to be sent
     *              accessToken Access token of logged in user
     * @return the Call
     */
    @POST("/cms/fiveStarRateSkill.json")
    Call<FiveStarSkillRatingResponse> fiveStarRateSkill(@QueryMap Map<String, String> query);

    /**
     * Reset Password call
     *
     * @param email
     * @param password
     * @param newPassword
     * @return the call
     */
    @POST("/aaa/changepassword.json")
    Call<ResetPasswordResponse> resetPasswordResponse(@Query("changepassword") String email,
                                                      @Query("password") String password,
                                                      @Query("newpassword") String newPassword);

    /**
     * Post feedback provided by user
     *
     * @param query A query map consisting of following key value pairs
     *              model       Model of the skill (e.g. general)
     *              group       Group of skill (e.g. Knowledge)
     *              language    Language directory in which the skill resides (e.g. en)
     *              skill       Skill Tag of object in which the skill data resides
     *              feedback    User feedback to be posted
     *              accessToken Access token of the logged in user
     * @return the Call
     */
    @POST("/cms/feedbackSkill.json")
    Call<PostSkillFeedbackResponse> postFeedback(@QueryMap Map<String, String> query);

    /**
     * @return list of groups
     */
    @GET("/cms/getGroups.json")
    Call<ListGroupsResponse> fetchListGroups();


    @GET("/cms/getSkillList.json")
    Call<ListSkillsResponse> fetchListSkills(@QueryMap Map<String, String> query);

    @GET("/cms/getRatingByUser.json")
    Call<GetRatingByUserResponse> getRatingByUser(@QueryMap Map<String, String> query);

    /**
     * Get feedback list from the server
     *
     * @param query A query map consisting of following key value pairs
     *              model       Model of the skill (e.g. general)
     *              group       Group of skill (e.g. Knowledge)
     *              language    Language directory in which the skill resides (e.g. en)
     *              skill       Skill Tag of object in which the skill data resides
     * @return the call
     */
    @GET("/cms/getSkillFeedback.json")
    Call<GetSkillFeedbackResponse> fetchFeedback(@QueryMap Map<String, String> query);

    /**
     * Get skills based on different metrics from the server
     *
     * @param model    Model of the skill (e.g. general)
     * @param language Language directory in which the skill resides (e.g. en)
     * @return the call
     */
    @GET("/cms/getSkillMetricsData.json")
    Call<ListSkillMetricsResponse> fetchSkillMetricsData(@Query("model") String model,
                                                         @Query("language") String language);

    /**
     * Report a skill
     * @param query A query  map consisting of the following key value pairs
     *              model       Model of the skill(Eg. general)
     *              group       Group of the skill(Eg. Knowledge)
     *              skill       Skill to be reported
     *              feedback    The report/msg send by the user
     *              accessToken access token of the user
     * @return the call
     */
    @POST("cms/reportSkill.json")
    Call<ReportSkillResponse> reportSkill(@QueryMap Map<String, String> query);

}
