
package org.fossasia.susi.ai.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class Answer {

    @SerializedName("data")
    @Expose
    List<Datum> data = new ArrayList<Datum>();
    @SerializedName("metadata")
    @Expose
    Metadata metadata;
    @SerializedName("actions")
    @Expose
    List<Action> actions = new ArrayList<Action>();

    public List<Datum> getData() {
        return data;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public List<Action> getActions() {
        return actions;
    }
}
