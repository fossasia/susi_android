package org.fossasia.susi.ai.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mayank on 09-12-2016.
 */

public class RelatedTopics {

    @SerializedName("FirstURL")
    @Expose
    private String des;

    @SerializedName("Text")
    @Expose
    private String text;

    @SerializedName("Icon")
    @Expose
    private WebIcon icon;


    public RelatedTopics(String des, String text, WebIcon icon) {
        this.des = des;
        this.text = text;
        this.icon = icon;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
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
}
