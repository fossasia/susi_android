package org.fossasia.susi.ai.model;

import io.realm.RealmObject;

/**
 * Created by chiragw15 on 9/6/17.
 */

public class MapData {

    private double latitude;
    private double longitude;
    private double zoom;

    public MapData(double latitude, double longitude, double zoom) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.zoom = zoom;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }
}
