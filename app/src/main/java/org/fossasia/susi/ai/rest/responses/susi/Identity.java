package org.fossasia.susi.ai.rest.responses.susi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Identity {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("anonymous")
    @Expose
    private boolean anonymous;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean getAnonymous() {
        return anonymous;
    }
}
