package org.fossasia.susi.ai.rest.responses.susi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by meeera on 30/6/17.
 */

public class UserSetting {
    @SerializedName("session")
    @Expose
    private Session session;
    @SerializedName("settings")
    @Expose
    private Settings settings;

    /**
     * Gets session
     *
     * @return the session
     */
    public Session getSession() {
        return session;
    }

    /**
     * Gets settings
     *
     * @return the settings
     */
    public Settings getSettings() {
        return settings;
    }
}
