package org.fossasia.susi.ai.rest.responses.susi;


import io.realm.RealmObject;

/**
 * <h1>Kotlin Data class to parse data object in retrofit response from susi client.</h1>
 */
public class Datum extends RealmObject {


    private String _0;

    private String _1;

    private String answer;

    private String query;

    private double lon;

    private String place;

    private double lat;

    private int population;

    private float percent;

    private String president;

    private String title;

    private String description;

    private String link;

    /**
     * Gets 0.
     *
     * @return the 0
     */
    public String get_0() {
        return _0;
    }

    /**
     * Gets 1.
     *
     * @return the 1
     */
    public String get_1() {
        return _1;
    }

    /**
     * Gets answer.
     *
     * @return the answer
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * Gets query.
     *
     * @return the query
     */
    public String getQuery() {
        return query;
    }

    /**
     * Gets lon.
     *
     * @return the lon
     */
    public double getLon() {
        return lon;
    }

    /**
     * Gets place.
     *
     * @return the place
     */
    public String getPlace() {
        return place;
    }

    /**
     * Gets lat.
     *
     * @return the lat
     */
    public double getLat() {
        return lat;
    }

    /**
     * Gets population.
     *
     * @return the population
     */
    public int getPopulation() {
        return population;
    }

    /**
     * Gets percent.
     *
     * @return the percent
     */
    public float getPercent() {
        return percent;
    }

    /**
     * Gets president.
     *
     * @return the president
     */
    public String getPresident() {
        return president;
    }

    /**
     * Gets title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title.
     *
     * @param title the title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets link.
     *
     * @return the link
     */
    public String getLink() {
        return link;
    }

    /**
     * Sets link.
     *
     * @param link the link
     */
    public void setLink(String link) {
        this.link = link;
    }

}
