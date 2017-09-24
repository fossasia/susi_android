package org.fossasia.susi.ai.data.model;

/**
 * <h1>Class to store data in case of map action type.</h1>
 *
 * Created by chiragw15 on 9/6/17.
 */
public class MapData {

    private double latitude;
    private double longitude;
    private double zoom;

    /**
     * Instantiates a new Map data.
     *
     * @param latitude  the latitude
     * @param longitude the longitude
     * @param zoom      the zoom
     */
    public MapData(double latitude, double longitude, double zoom) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.zoom = zoom;
    }

    /**
     * Gets latitude.
     *
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets latitude.
     *
     * @param latitude the latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets longitude.
     *
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets longitude.
     *
     * @param longitude the longitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets zoom.
     *
     * @return the zoom
     */
    public double getZoom() {
        return zoom;
    }

    /**
     * Sets zoom.
     *
     * @param zoom the zoom
     */
    public void setZoom(double zoom) {
        this.zoom = zoom;
    }
}
