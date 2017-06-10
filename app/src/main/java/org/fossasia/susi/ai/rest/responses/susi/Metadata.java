package org.fossasia.susi.ai.rest.responses.susi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Metadata {

    @SerializedName("count")
    @Expose
    private int count;

    public int getCount() {
        return count;
    }
}
