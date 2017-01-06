package org.fossasia.susi.ai.model;

import io.realm.RealmObject;

/**
 * Created by mayanktripathi on 05/01/17.
 */

public class WebSearchModel extends RealmObject {
    private String url;
    private String headline;
    private String body;
    private String imageURL;

    public WebSearchModel() {
    }

    public WebSearchModel(String url, String headline, String body, String imageUrl) {
        this.url = url;
        this.headline = headline;
        this.body = body;
        this.imageURL = imageUrl;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getUrl() {
        return url;
    }

    public String getHeadline() {
        return headline;
    }

    public String getBody() {
        return body;
    }

    public String getImageURL() {
        return imageURL;
    }
}
