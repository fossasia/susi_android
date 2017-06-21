package org.fossasia.susi.ai.rest.responses.susi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * <h1>POJO class to parse retrofit response from memory endpoint susi client.</h1>
 *
 * Created by chiragw15 on 25/5/17.
 */
public class MemoryResponse {
    @SerializedName("cognitions")
    @Expose
    private List <SusiResponse> cognitionsList;

    @SerializedName("session")
    @Expose
    private Session session;

    /**
     * Gets cognitions list.
     *
     * @return the cognitions list
     */
    public List<SusiResponse> getCognitionsList() {
        return cognitionsList;
    }

    /**
     * Sets cognitions list.
     *
     * @param cognitionsList the cognitions list
     */
    public void setCognitionsList(List<SusiResponse> cognitionsList) {
        this.cognitionsList = cognitionsList;
    }

    /**
     * Gets session.
     *
     * @return the session
     */
    public Session getSession() {
        return session;
    }

    /**
     * Sets session.
     *
     * @param session the session
     */
    public void setSession(Session session) {
        this.session = session;
    }
}
