package org.fossasia.susi.ai.helper

import org.fossasia.susi.ai.data.model.MapData

/**
 * <h1>Helper class to get map image and link.</h1>
 */
class MapHelper
/**
 * Instantiates a new Map helper.

 * @param mapData the map data
 */
(mapData: MapData?) {
    /**
     * Gets map url.

     * @return the map url
     */
    var mapURL: String? = null
        private set
    /**
     * Gets web link.

     * @return the web link
     */
    var webLink: String? = null
        private set
    /**
     * Is parse successful boolean.

     * @return the boolean
     */
    var isParseSuccessful: Boolean = false
        private set

    init {
        if (mapData == null)
            isParseSuccessful = false
        else {
            val latitude = mapData.latitude
            val longitude = mapData.longitude
            val zoom = mapData.zoom
            mapURL = String.format(URL_SCHEME, latitude, longitude, zoom, width, height)
            webLink = String.format(MAP_URL_SCHEME, zoom, latitude, longitude)
            isParseSuccessful = true
        }
    }

    companion object {
        private val BASE_URL = "http://staticmap.openstreetmap.de/staticmap.php?"
        private val URL_SCHEME = BASE_URL + "center=%f,%f&zoom=%f&size=%sx%s"
        private val MAP_URL = "https://www.openstreetmap.org/#map="
        private val MAP_URL_SCHEME = MAP_URL + "%f/%f/%f"
        private val width = 340
        private val height = 170
    }
}
