package org.fossasia.susi.ai.rest.responses.susi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * <h1>POJO class to parse retrofit response from login endpoint susi client.</h1>
 *
 * Created by saurabh on 12/10/16.
 */
public class LoginResponse {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("session")
    @Expose
    private Session session;
    @SerializedName("valid_seconds")
    @Expose
    private long validSeconds;
    /**
     * The Access token.
     */
    @SerializedName("access_token")
    @Expose
    String accessToken;

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets session.
     *
     * @return the session
     */
    public Session getSession() {
        return session;
    }

    /**
     * Gets valid seconds.
     *
     * @return the valid seconds
     */
    public long getValidSeconds() {
        return validSeconds;
    }

    /**
     * Gets access token.
     *
     * @return the access token
     */
    public String getAccessToken() {
        return accessToken;
    }
}
