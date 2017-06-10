package org.fossasia.susi.ai.rest.responses.susi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
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
    @SerializedName("access_token")
    @Expose
    String accessToken;

    public String getMessage() {
        return message;
    }

    public Session getSession() {
        return session;
    }

    public long getValidSeconds() {
        return validSeconds;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
