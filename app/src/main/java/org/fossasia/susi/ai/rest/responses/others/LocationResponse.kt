package org.fossasia.susi.ai.rest.responses.others

import com.google.gson.annotations.SerializedName

/**
 * <h1>POJO class to parse retrofit response from location client.</h1>

 * Created by chiragw15 on 6/12/16.
 */
class LocationResponse {
    /**
     * Gets ip.

     * @return the ip
     */
    /**
     * Sets ip.

     * @param ip the ip
     */
    @SerializedName("ip")
    var ip: String? = null
    /**
     * Gets hostname.

     * @return the hostname
     */
    /**
     * Sets hostname.

     * @param hostname the hostname
     */
    @SerializedName("hostname")
    var hostname: String? = null
    /**
     * Gets city.

     * @return the city
     */
    /**
     * Sets city.

     * @param city the city
     */
    @SerializedName("city")
    var city: String? = null
    /**
     * Gets region.

     * @return the region
     */
    /**
     * Sets region.

     * @param region the region
     */
    @SerializedName("region")
    var region: String? = null
    /**
     * Gets country.

     * @return the country
     */
    /**
     * Sets country.

     * @param country the country
     */
    @SerializedName("country")
    var country: String? = null
    /**
     * Gets loc.

     * @return the loc
     */
    /**
     * Sets loc.

     * @param loc the loc
     */
    @SerializedName("loc")
    var loc: String? = null
    /**
     * Gets org.

     * @return the org
     */
    /**
     * Sets org.

     * @param org the org
     */
    @SerializedName("org")
    var org: String? = null
    /**
     * Gets postal.

     * @return the postal
     */
    /**
     * Sets postal.

     * @param postal the postal
     */
    @SerializedName("postal")
    var postal: String? = null
}
