package org.fossasia.susi.ai.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Session {

    @SerializedName("identity")
    @Expose
    Identity identity;

    public Identity getIdentity() {
        return identity;
    }
}
