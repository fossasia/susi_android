package org.fossasia.susi.ai.model;

/**
 * Created by meeera on 10/6/17.
 */

public class MapModel {
    private String latitude;
    private String longitude;
    private String zoom;

    public MapModel(String latitude, String longitude, String zoom) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.zoom = zoom;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getZoom() {
        return zoom;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setZoom(String zoom) {
        this.zoom = zoom;
    }
}
