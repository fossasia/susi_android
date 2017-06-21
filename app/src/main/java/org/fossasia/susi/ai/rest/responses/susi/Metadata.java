package org.fossasia.susi.ai.rest.responses.susi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * <h1>POJO class to parse meta data object in retrofit response from susi client.</h1>
 */
public class Metadata {

    @SerializedName("count")
    @Expose
    private int count;

    /**
     * Gets count.
     *
     * @return the count
     */
    public int getCount() {
        return count;
    }
}
