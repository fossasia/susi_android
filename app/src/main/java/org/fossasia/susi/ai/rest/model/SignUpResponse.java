package org.fossasia.susi.ai.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by saurabh on 12/10/16.
 */

public class SignUpResponse {
    @SerializedName("message")
    @Expose
    String message;
    @SerializedName("session")
    @Expose
    Session session;

    public String getMessage() {
        return message;
    }

    public Session getSession() {
        return session;
    }
}
