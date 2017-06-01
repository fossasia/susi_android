package org.fossasia.susi.ai.rest.responses.others;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mayanktripathi on 30/01/17.
 */

public class VideoSearch {

    @SerializedName("items")
    @Expose
    private List<ItemsList> items;

    public List<ItemsList> getItems() {
        return items;
    }

    public void setItems(List<ItemsList> items) {
        this.items = items;
    }
}
