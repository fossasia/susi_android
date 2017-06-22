package org.fossasia.susi.ai.rest.responses.susi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * <h1>POJO class to parse retrofit response from forgot password endpoint susi client.</h1>
 */
public class ForgotPasswordResponse {
    @SerializedName("message")
    @Expose
    private String message;

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }
}
