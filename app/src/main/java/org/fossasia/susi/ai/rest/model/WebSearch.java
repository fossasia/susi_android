package org.fossasia.susi.ai.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mayank on 12-12-2016.
 */

public class WebSearch{

    @SerializedName("Heading")
    @Expose
    private String heading;

    @SerializedName("RelatedTopics")
    @Expose
    private List<RelatedTopics> relatedTopics;

    public WebSearch(String heading, List<RelatedTopics> relatedTopics) {
        this.heading = heading;
        this.relatedTopics = relatedTopics;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public List<RelatedTopics> getRelatedTopics() {
        return relatedTopics;
    }

    public void setRelatedTopics(List<RelatedTopics> relatedTopics) {
        this.relatedTopics = relatedTopics;
    }
}
