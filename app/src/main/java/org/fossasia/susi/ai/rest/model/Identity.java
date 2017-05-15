package org.fossasia.susi.ai.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Identity {

    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("type")
    @Expose
    String type;
    @SerializedName("anonymous")
    @Expose
    Boolean anonymous;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Boolean getAnonymous() {
        return anonymous;
    }
}
