package org.fossasia.susi.ai.rest.responses.susi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * <h1>POJO class to parse action types in retrofit response from susi client.</h1>
 */
public class Action {

    @SerializedName("delay")
    @Expose
    private long delay;
    @SerializedName("expression")
    @Expose
    private String expression;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("link")
    @Expose
    private String anchorLink;
    @SerializedName("text")
    @Expose
    private String anchorText;
    @SerializedName("query")
    @Expose
    private String query;
    @SerializedName("latitude")
    @Expose
    private double latitude;
    @SerializedName("longitude")
    @Expose
    private double longitude;
    @SerializedName("zoom")
    @Expose
    private double zoom;
    @SerializedName("count")
    @Expose
    private int count;

    /**
     * Gets expression.
     *
     * @return the expression
     */
    public String getExpression() {
        return expression;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Gets delay.
     *
     * @return the delay
     */
    public long getDelay() {
        return delay;
    }

    /**
     * Gets anchor link.
     *
     * @return the anchor link
     */
    public String getAnchorLink() {
        return anchorLink;
    }

    /**
     * Gets anchor text.
     *
     * @return the anchor text
     */
    public String getAnchorText() {
        return anchorText;
    }

    /**
     * Gets query.
     *
     * @return the query
     */
    public String getQuery() {
        return query;
    }

    /**
     * Gets latitude.
     *
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Gets longitude.
     *
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Gets zoom.
     *
     * @return the zoom
     */
    public double getZoom() {
        return zoom;
    }

    public int getCount() {
        return count;
    }
}
