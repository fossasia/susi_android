package org.fossasia.susi.ai.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by chiragw15 on 25/5/17.
 */
public class MemoryResponse {
    @SerializedName("cognitions")
    @Expose
    List <SusiResponse> cognitionsList;

    @SerializedName("session")
    @Expose
    Session session;

    public List<SusiResponse> getCognitionsList() {
        return cognitionsList;
    }

    public void setCognitionsList(List<SusiResponse> cognitionsList) {
        this.cognitionsList = cognitionsList;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
