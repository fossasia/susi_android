package org.fossasia.susi.ai.rest.responses.others;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mayank on 09-12-2016.
 */

public class RelatedTopics {

    @SerializedName("FirstURL")
    @Expose
    private String url;

    @SerializedName("Text")
    @Expose
    private String text;

    @SerializedName("Icon")
    @Expose
    private WebIcon icon;

    @SerializedName("Result")
    @Expose
    private String result;

    public RelatedTopics(String url, String text, WebIcon icon) {
        this.url = url;
        this.text = text;
        this.icon = icon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public WebIcon getIcon() {
        return icon;
    }

    public void setIcon(WebIcon icon) {
        this.icon = icon;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
