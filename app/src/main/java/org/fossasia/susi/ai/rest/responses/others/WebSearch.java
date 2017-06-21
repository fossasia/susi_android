package org.fossasia.susi.ai.rest.responses.others;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * <h1>POJO class to parse retrofit response from websearch client.</h1>
 *
 * Created by mayank on 12-12-2016.
 */
public class WebSearch{

    @SerializedName("Heading")
    @Expose
    private String heading;

    @SerializedName("RelatedTopics")
    @Expose
    private List<RelatedTopics> relatedTopics;

    /**
     * Instantiates a new Web search.
     *
     * @param heading       the heading
     * @param relatedTopics the related topics
     */
    public WebSearch(String heading, List<RelatedTopics> relatedTopics) {
        this.heading = heading;
        this.relatedTopics = relatedTopics;
    }

    /**
     * Gets heading.
     *
     * @return the heading
     */
    public String getHeading() {
        return heading;
    }

    /**
     * Sets heading.
     *
     * @param heading the heading
     */
    public void setHeading(String heading) {
        this.heading = heading;
    }

    /**
     * Gets related topics.
     *
     * @return the related topics
     */
    public List<RelatedTopics> getRelatedTopics() {
        return relatedTopics;
    }

    /**
     * Sets related topics.
     *
     * @param relatedTopics the related topics
     */
    public void setRelatedTopics(List<RelatedTopics> relatedTopics) {
        this.relatedTopics = relatedTopics;
    }
}
