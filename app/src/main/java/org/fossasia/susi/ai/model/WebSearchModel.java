package org.fossasia.susi.ai.model;

import io.realm.RealmObject;

/**
 * <h1>Class to store data from websearch result.</h1>
 *
 * Created by mayanktripathi on 05/01/17.
 */
public class WebSearchModel extends RealmObject {
    private String url;
    private String headline;
    private String body;
    private String imageURL;

    /**
     * Instantiates a new Web search model.
     */
    public WebSearchModel() {
    }

    /**
     * Instantiates a new Web search model.
     *
     * @param url      the url
     * @param headline the headline
     * @param body     the body
     * @param imageUrl the image url
     */
    public WebSearchModel(String url, String headline, String body, String imageUrl) {
        this.url = url;
        this.headline = headline;
        this.body = body;
        this.imageURL = imageUrl;
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
     * Sets headline.
     *
     * @param headline the headline
     */
    public void setHeadline(String headline) {
        this.headline = headline;
    }

    /**
     * Sets body.
     *
     * @param body the body
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Sets image url.
     *
     * @param imageURL the image url
     */
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
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
     * Gets headline.
     *
     * @return the headline
     */
    public String getHeadline() {
        return headline;
    }

    /**
     * Gets body.
     *
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /**
     * Gets image url.
     *
     * @return the image url
     */
    public String getImageURL() {
        return imageURL;
    }
}
