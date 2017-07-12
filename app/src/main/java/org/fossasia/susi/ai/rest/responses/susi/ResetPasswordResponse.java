package org.fossasia.susi.ai.rest.responses.susi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by meeera on 13/7/17.
 */

public class ResetPasswordResponse {
    @SerializedName("session")
    @Expose
    private Session session;
    @SerializedName("accepted")
    @Expose
    private boolean accepted;
    @SerializedName("message")
    @Expose
    private String message;

    /**
     * Gets session.
     *
     * @return the session
     */
    public Session getSession() {
        return session;
    }

    /**
     * Gets accept.
     *
     * @return the accept
     */
    public boolean isAccepted() {
        return accepted;
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

}
