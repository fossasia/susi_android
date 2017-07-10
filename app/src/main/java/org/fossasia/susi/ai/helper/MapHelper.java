package org.fossasia.susi.ai.helper;

import android.support.annotation.Nullable;

import org.fossasia.susi.ai.data.model.MapData;

/**
 * <h1>Helper class to get map image and link.</h1>
 */
public class MapHelper {
    private static final String BASE_URL = "http://staticmap.openstreetmap.de/staticmap.php?";
    private static final String URL_SCHEME = BASE_URL + "center=%f,%f&zoom=%f&size=%sx%s";
    private static final String MAP_URL = "https://www.openstreetmap.org/#map=";
    private static final String MAP_URL_SCHEME = MAP_URL + "%f/%f/%f";
    private static final int size = 300;
    private String mapUrl;
    private String webLink;
    private boolean isParseSuccessful;

    /**
     * Instantiates a new Map helper.
     *
     * @param mapData the map data
     */
    public MapHelper(MapData mapData) {
        if(mapData == null)
            isParseSuccessful = false;
        else {
            double latitude = mapData.getLatitude();
            double longitude = mapData.getLongitude();
            double zoom = mapData.getZoom();
            mapUrl = String.format(URL_SCHEME, latitude, longitude, zoom, size, size);
            webLink = String.format(MAP_URL_SCHEME,zoom,latitude,longitude);
            isParseSuccessful = true;
        }
    }

    /**
     * Is parse successful boolean.
     *
     * @return the boolean
     */
    public boolean isParseSuccessful() {
        return isParseSuccessful;
    }

    /**
     * Gets map url.
     *
     * @return the map url
     */
    @Nullable
    public String getMapURL() {
        return mapUrl;

    }

    /**
     * Gets web link.
     *
     * @return the web link
     */
    @Nullable
    public String getWebLink() {
        return webLink;

    }

}
