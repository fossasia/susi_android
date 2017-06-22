package org.fossasia.susi.ai.rest.responses.others;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * <h1>POJO class to parse related topics object in retrofit response from websearch client.</h1>
 *
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

    /**
     * Instantiates a new Related topics.
     *
     * @param url  the url
     * @param text the text
     * @param icon the icon
     */
    public RelatedTopics(String url, String text, WebIcon icon) {
        this.url = url;
        this.text = text;
        this.icon = icon;
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

    /**
     * Gets text.
     *
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * Sets text.
     *
     * @param text the text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Gets icon.
     *
     * @return the icon
     */
    public WebIcon getIcon() {
        return icon;
    }

    /**
     * Sets icon.
     *
     * @param icon the icon
     */
    public void setIcon(WebIcon icon) {
        this.icon = icon;
    }

    /**
     * Gets result.
     *
     * @return the result
     */
    public String getResult() {
        return result;
    }

    /**
     * Sets result.
     *
     * @param result the result
     */
    public void setResult(String result) {
        this.result = result;
    }
}
