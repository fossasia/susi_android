package org.fossasia.susi.ai.rest.responses.susi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Session {

    @SerializedName("identity")
    @Expose
    private Identity identity;

    public Identity getIdentity() {
        return identity;
    }
}
