package org.fossasia.susi.ai.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mayanktripathi on 30/01/17.
 */
public class YoutubeId {

    @SerializedName("kind")
    @Expose
    private String kind;

    @SerializedName("videoId")
    @Expose
    private String videoId;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
}
