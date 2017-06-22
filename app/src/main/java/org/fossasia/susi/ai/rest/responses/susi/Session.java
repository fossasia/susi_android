package org.fossasia.susi.ai.rest.responses.susi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * <h1>POJO class to parse session object in retrofit response from susi client.</h1>
 */
public class Session {

    @SerializedName("identity")
    @Expose
    private Identity identity;

    /**
     * Gets identity.
     *
     * @return the identity
     */
    public Identity getIdentity() {
        return identity;
    }
}
