package org.fossasia.susi.ai.rest.responses.susi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Datum extends RealmObject {

    @SerializedName("0")
    @Expose
    private String _0;
    @SerializedName("1")
    @Expose
    private String _1;
    @SerializedName("answer")
    @Expose
    private String answer;
    @SerializedName("query")
    @Expose
    private String query;
    @SerializedName("lon")
    @Expose
    private double lon;
    @SerializedName("place")
    @Expose
    private String place;
    @SerializedName("lat")
    @Expose
    private double lat;
    @SerializedName("population")
    @Expose
    private int population;
    @SerializedName("percent")
    @Expose
    private float percent;
    @SerializedName("president")
    @Expose
    private String president;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("link")
    @Expose
    private String link;

    public String get_0() {
        return _0;
    }

    public String get_1() {
        return _1;
    }

    public String getAnswer() {
        return answer;
    }

    public String getQuery() {
        return query;
    }

    public double getLon() {
        return lon;
    }

    public String getPlace() {
        return place;
    }

    public double getLat() {
        return lat;
    }

    public int getPopulation() {
        return population;
    }

    public float getPercent() {
        return percent;
    }

    public String getPresident() {
        return president;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
