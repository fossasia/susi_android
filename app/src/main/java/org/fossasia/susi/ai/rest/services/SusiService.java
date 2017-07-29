package org.fossasia.susi.ai.rest.services;

import org.fossasia.susi.ai.rest.clients.BaseUrl;
import org.fossasia.susi.ai.rest.responses.susi.ChangeSettingResponse;
import org.fossasia.susi.ai.rest.responses.susi.ForgotPasswordResponse;
import org.fossasia.susi.ai.rest.responses.susi.LoginResponse;
import org.fossasia.susi.ai.rest.responses.susi.SignUpResponse;
import org.fossasia.susi.ai.rest.responses.susi.SusiBaseUrls;
import org.fossasia.susi.ai.rest.responses.susi.SusiResponse;
import org.fossasia.susi.ai.rest.responses.susi.MemoryResponse;
import org.fossasia.susi.ai.rest.responses.susi.UserSetting;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


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
     * @param timezoneOffset the timezone offset
     * @param longitude      the longitude
     * @param latitude       the latitude
     * @param geosource      the geosource
     * @param language       the language
     * @param query          the query
     * @return the susi response
     */
    @GET("/susi/chat.json")
    Call<SusiResponse> getSusiResponse(@Query("timezoneOffset") int timezoneOffset,
                                       @Query("longitude") double longitude,
                                       @Query("latitude") double latitude,
                                       @Query("geosource") String geosource,
                                       @Query("language") String language,
                                       @Query("q") String query);

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
    Call<ChangeSettingResponse> changeSettingResponse(@Query("key") String key,
                                                      @Query("value") String value);

    /**
     * Get User settings
     *
     * @return settings
     */
    @GET("/aaa/listUserSettings.json")
    Call<UserSetting> getUserSetting();
}
