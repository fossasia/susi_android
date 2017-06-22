package org.fossasia.susi.ai.rest.responses.others;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * <h1>POJO class to parse web icon object in retrofit response from websearch client.</h1>
 *
 * Created by mayank on 12-12-2016.
 */
public class WebIcon {

    @SerializedName("URL")
    @Expose
    private String url;

    /**
     * Instantiates a new Web icon.
     *
     * @param url the url
     */
    public WebIcon(String url) {
        this.url = url;
    }

    /**
     * Gets url.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets url.
     *
     * @param url the url
     */
    public void setUrl(String url) {
        this.url = url;
    }
}

