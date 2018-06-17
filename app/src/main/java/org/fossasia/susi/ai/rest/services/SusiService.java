package org.fossasia.susi.ai.rest.services;

import org.fossasia.susi.ai.rest.clients.BaseUrl;
import org.fossasia.susi.ai.rest.responses.susi.ChangeSettingResponse;
import org.fossasia.susi.ai.rest.responses.susi.FiveStarSkillRatingResponse;
import org.fossasia.susi.ai.rest.responses.susi.ForgotPasswordResponse;
import org.fossasia.susi.ai.rest.responses.susi.GetRatingByUserResponse;
import org.fossasia.susi.ai.rest.responses.susi.ListGroupsResponse;
import org.fossasia.susi.ai.rest.responses.susi.ListSkillsResponse;
import org.fossasia.susi.ai.rest.responses.susi.LoginResponse;
import org.fossasia.susi.ai.rest.responses.susi.MemoryResponse;
import org.fossasia.susi.ai.rest.responses.susi.ResetPasswordResponse;
import org.fossasia.susi.ai.rest.responses.susi.SignUpResponse;
import org.fossasia.susi.ai.rest.responses.susi.SkillRatingResponse;
import org.fossasia.susi.ai.rest.responses.susi.SusiBaseUrls;
import org.fossasia.susi.ai.rest.responses.susi.SusiResponse;
import org.fossasia.susi.ai.rest.responses.susi.TableSusiResponse;
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
     * Gets susi base urls.
     *
     * @return the susi base urls
     */
    @GET(BaseUrl.SUSI_SERVICES_URL + "/config_susi.json")
    Call<SusiBaseUrls> getSusiBaseUrls();

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
    Call<TableSusiResponse> getTableSusiResponse(@QueryMap Map<String, String> query);

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
     * @param model    Model of Skill
     * @param group    Group of Skill
     * @param language Language of Skill
     * @param skill    Skill name
     * @param rating   Rating either positive or negative
     * @return the Call
     */
    @POST("/cms/rateSkill.json")
    Call<SkillRatingResponse> rateSkill(@Query("model") String model,
                                        @Query("group") String group,
                                        @Query("language") String language,
                                        @Query("skill") String skill,
                                        @Query("rating") String rating);

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
     * @return list of groups
     */
    @GET("/cms/getGroups.json")
    Call<ListGroupsResponse> fetchListGroups();


    @GET("/cms/getSkillList.json")
    Call<ListSkillsResponse> fetchListSkills(@Query("group") String groups,
                                             @Query("applyFilter") String applyFilter,
                                             @Query("filter_name") String filterName,
                                             @Query("filter_type") String filterType);

    @GET("/cms/getRatingByUser.json")
    Call<GetRatingByUserResponse> getRatingByUser(@QueryMap Map<String, String> query);

}
