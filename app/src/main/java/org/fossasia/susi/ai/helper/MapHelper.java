package org.fossasia.susi.ai.helper;

import android.support.annotation.Nullable;
import android.util.Log;

import org.fossasia.susi.ai.model.MapData;

public class MapHelper {
    private static final StringBuilder BASE_URL = new StringBuilder("http://staticmap.openstreetmap.de/staticmap.php?");
    private static final StringBuilder URL_SCHEME = BASE_URL.append("center=%f,%f&zoom=%f&size=%sx%s");
    private static final StringBuilder MAP_URL = new StringBuilder("https://www.openstreetmap.org/#map=");
    private static final StringBuilder MAP_URL_SCHEME = MAP_URL.append("%f/%f/%f");
    private static final int size = 300;
    private String mapUrl;
    private String webLink;
    private boolean isParseSuccessful;

    public MapHelper(MapData mapData) {
        if(mapData == null)
            isParseSuccessful = false;
        else {
            double latitude = mapData.getLatitude();
            double longitude = mapData.getLongitude();
            double zoom = mapData.getZoom();
            mapUrl = String.format(URL_SCHEME.toString(), latitude, longitude, zoom, size, size);
            webLink = String.format(MAP_URL_SCHEME.toString(),zoom,latitude,longitude);
            isParseSuccessful = true;
        }
    }

    public boolean isParseSuccessful() {
        return isParseSuccessful;
    }

    @Nullable
    public String getMapURL() {
        return mapUrl;

    }

    @Nullable
    public String getWebLink() {
        return webLink;

    }

}
