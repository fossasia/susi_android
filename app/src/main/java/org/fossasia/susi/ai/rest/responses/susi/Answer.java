package org.fossasia.susi.ai.rest.responses.susi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

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

    public RealmList<Datum> getData() {
        return data;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public List<Action> getActions() {
        return actions;
    }
}
