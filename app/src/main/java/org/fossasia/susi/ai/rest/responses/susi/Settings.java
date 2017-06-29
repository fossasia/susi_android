package org.fossasia.susi.ai.rest.responses.susi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by meeera on 30/6/17.
 */

public class Settings {
    @SerializedName("speech_always")
    @Expose
    private String speechAlways;
    @SerializedName("enter_send")
    @Expose
    private String enterSend;
    @SerializedName("speech_output")
    @Expose
    private String speechOutput;
    @SerializedName("mic_input")
    @Expose
    private String micInput;
    @SerializedName("theme")
    @Expose
    private String theme;

    /**
     * Gets enterSend
     *
     * @return the enterSend
     */
    public String getEnterSend() {
        return enterSend;
    }

    /**
     * Gets speechOutput
     *
     * @return the speechOutput
     */
    public String getSpeechOutput() {
        return speechOutput;
    }

    /**
     * Gets speechAlways
     *
     * @return the speechAlways
     */
    public String getSpeechAlways() {
        return speechAlways;
    }

    public String getTheme() {
        return theme;
    }

    public String getMicInput() {
        return micInput;
    }
}
