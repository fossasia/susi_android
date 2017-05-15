package org.fossasia.susi.ai.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mayanktripathi on 30/01/17.
 */

public class ItemsList {

    @SerializedName("id")
    @Expose
    private YoutubeId id;

    public YoutubeId getId() {
        return id;
    }

    public void setId(YoutubeId id) {
        this.id = id;
    }
}
