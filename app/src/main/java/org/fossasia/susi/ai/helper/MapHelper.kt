package org.fossasia.susi.ai.helper

import org.fossasia.susi.ai.BuildConfig
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
            mapURL = String.format(URL_SCHEME, longitude, latitude, zoom, width, height)
            webLink = String.format(MAP_URL_SCHEME, zoom, latitude, longitude)
            isParseSuccessful = true
        }
    }

    companion object {
        val MAPBOX_STATIC_MAP_KEY = BuildConfig.MAPBOX_API_KEY // create your own token by register on www.mapbox.com
        private const val BASE_URL = "https://api.mapbox.com/styles/v1/mapbox/streets-v10/static/"
        private var URL_SCHEME = BASE_URL + "%f,%f,%f/%sx%s?access_token=" + MAPBOX_STATIC_MAP_KEY
        private const val MAP_URL = "https://www.openstreetmap.org/#map="
        private val MAP_URL_SCHEME = "$MAP_URL%f/%f/%f"
        private const val width = 340
        private const val height = 170
    }
}
