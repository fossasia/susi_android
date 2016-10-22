package org.fossasia.susi.ai.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by saurabh on 12/10/16.
 */

public class LoginResponse {
    @SerializedName("message")
    @Expose
    String message;
    @SerializedName("session")
    @Expose
    Session session;
    @SerializedName("valid_seconds")
    @Expose
    Long validSeconds;
    @SerializedName("access_token")
    @Expose
    String accessToken;

    public String getMessage() {
        return message;
    }

    public Session getSession() {
        return session;
    }

    public Long getValidSeconds() {
        return validSeconds;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
