package org.fossasia.susi.ai.rest.responses.susi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * <h1>POJO class to parse retrofit response from sign up endpoint susi client.</h1>
 *
 * Created by saurabh on 12/10/16.
 */
public class SignUpResponse {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("session")
    @Expose
    private Session session;

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
}
