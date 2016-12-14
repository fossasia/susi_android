
package org.fossasia.susi.ai.rest.model;


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
    @SerializedName("text")
    @Expose
    private String anchorText;
    @SerializedName("link")
    @Expose
    private String anchorLink;

    public String getExpression() {
        return expression;
    }

    public String getType() {
        return type;
    }

    public long getDelay() {
        return delay;
    }


    public String getAnchorText() {
        return anchorText;
    }

    public String getAnchorLink() {
        return anchorLink;
    }
}
