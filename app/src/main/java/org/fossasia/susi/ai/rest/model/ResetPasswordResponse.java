package org.fossasia.susi.ai.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResetPasswordResponse {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("regex")
    @Expose
    private String regex;
    @SerializedName("regexTooltip")
    @Expose
    private String regexTooltip;

    public String getMessageDetails() {
        return message;
    }

    public String getRegex() {
        return regex;
    }

    public String getRegexTooltip() {
        return regexTooltip;
    }
}
