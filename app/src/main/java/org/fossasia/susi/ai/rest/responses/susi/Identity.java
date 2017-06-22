package org.fossasia.susi.ai.rest.responses.susi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * <h1>POJO class to parse identity object in retrofit response from susi client.</h1>
 */
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

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets anonymous.
     *
     * @return the anonymous
     */
    public boolean getAnonymous() {
        return anonymous;
    }
}
