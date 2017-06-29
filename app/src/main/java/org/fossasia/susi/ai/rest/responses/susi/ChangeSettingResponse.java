package org.fossasia.susi.ai.rest.responses.susi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by meeera on 30/6/17.
 */

public class ChangeSettingResponse {
    @SerializedName("session")
    @Expose
    private Session session;
    @SerializedName("message")
    @Expose
    private String message;

    public Session getSession() {
        return session;
    }

    public String getMessage() {
        return message;
    }
}
