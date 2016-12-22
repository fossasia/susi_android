package org.fossasia.susi.ai.helper;

import android.support.annotation.Nullable;
import android.util.Log;

public class MapHelper {
    private static final String TAG = MapHelper.class.getSimpleName();
    private static final String BASE_URL = "http://staticmap.openstreetmap.de/staticmap.php?";
    private static final String URL_SCHEME = BASE_URL + "center=%s,%s&zoom=%s&size=%sx%s";
    private static final int size = 300;
    private String longitude;
    private String latitude;
    private String zoom;
    private String mapUrl;
    private String displayText;
    private boolean isParseSuccessful;
    private String webLink;

    public MapHelper(String text) {
        try {
            String arr[] = text.split("/");
            longitude = arr[arr.length - 1];
            latitude = arr[arr.length - 2];
            String zoomText[] = arr[arr.length - 3].split("=");
            zoom = zoomText[zoomText.length - 1];
            mapUrl = String.format(URL_SCHEME, latitude, longitude, zoom, size, size);
            isParseSuccessful = true;
        } catch (Exception e) {
            Log.d(TAG, text);
            e.printStackTrace();
            mapUrl = null;
            isParseSuccessful = false;
        }
        try {
            String texts[] = text.split("http");
            displayText = texts[0].trim();
            webLink = "http" + texts[1].trim();
        } catch (Exception e) {
            Log.d(TAG, text);
            e.printStackTrace();
            displayText = text;
        }
    }

    public String getWebLink() {
        return webLink;
    }

    public boolean isParseSuccessful() {
        return isParseSuccessful;
    }

    @Nullable
    public String getMapURL() {
        return mapUrl;

    }

    public String getDisplayText() {
        return displayText;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getZoom() {
        return zoom;
    }

}
