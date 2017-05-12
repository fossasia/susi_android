package org.fossasia.susi.ai.rest.model;

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
    private Double lon;
    @SerializedName("place")
    @Expose
    private String place;
    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("population")
    @Expose
    private Integer population;
    @SerializedName("percent")
    @Expose
    private Float percent;
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

    public Double getLon() {
        return lon;
    }

    public String getPlace() {
        return place;
    }

    public Double getLat() {
        return lat;
    }

    public Integer getPopulation() {
        return population;
    }

    public Float getPercent() {
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
