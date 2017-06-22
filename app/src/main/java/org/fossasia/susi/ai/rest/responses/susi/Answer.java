package org.fossasia.susi.ai.rest.responses.susi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

/**
 * <h1>POJO class to parse answer in retrofit response from susi client.</h1>
 */
public class Answer {

    @SerializedName("data")
    @Expose
    private RealmList<Datum> data = new RealmList<>();
    @SerializedName("metadata")
    @Expose
    private
    Metadata metadata;
    @SerializedName("actions")
    @Expose
    private List<Action> actions = new ArrayList<>();

    /**
     * Gets data.
     *
     * @return the data
     */
    public RealmList<Datum> getData() {
        return data;
    }

    /**
     * Gets metadata.
     *
     * @return the metadata
     */
    public Metadata getMetadata() {
        return metadata;
    }

    /**
     * Gets actions.
     *
     * @return the actions
     */
    public List<Action> getActions() {
        return actions;
    }
}
