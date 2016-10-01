
package org.fossasia.susi.ai.rest.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Action {

    @SerializedName("expression")
    @Expose
    String expression;
    @SerializedName("type")
    @Expose
    String type;

    public String getExpression() {
        return expression;
    }

    public String getType() {
        return type;
    }
}
