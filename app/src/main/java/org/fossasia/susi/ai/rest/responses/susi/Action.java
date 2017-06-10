package org.fossasia.susi.ai.rest.responses.susi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("zoom")
    @Expose
    private String zoom;

    public String getExpression() {
        return expression;
    }

    public String getType() {
        return type;
    }

    public long getDelay() {
        return delay;
    }

    public String getAnchorLink() {
        return anchorLink;
    }

    public String getAnchorText() {
        return anchorText;
    }

    public String getQuery() {
        return query;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getZoom() {
        return zoom;
    }
}
