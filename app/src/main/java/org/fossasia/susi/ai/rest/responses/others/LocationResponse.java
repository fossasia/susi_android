package org.fossasia.susi.ai.rest.responses.others;

import com.google.gson.annotations.SerializedName;

/**
 * <h1>POJO class to parse retrofit response from location client.</h1>
 *
 * Created by chiragw15 on 6/12/16.
 */
public class LocationResponse {
    @SerializedName("ip")
    private String ip;
    @SerializedName("hostname")
    private String hostname;
    @SerializedName("city")
    private String city;
    @SerializedName("region")
    private String region;
    @SerializedName("country")
    private String country;
    @SerializedName("loc")
    private String loc;
    @SerializedName("org")
    private String org;
    @SerializedName("postal")
    private String postal;


    /**
     * Gets ip.
     *
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * Sets ip.
     *
     * @param ip the ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Gets hostname.
     *
     * @return the hostname
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Sets hostname.
     *
     * @param hostname the hostname
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    /**
     * Gets city.
     *
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets city.
     *
     * @param city the city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets region.
     *
     * @return the region
     */
    public String getRegion() {
        return region;
    }

    /**
     * Sets region.
     *
     * @param region the region
     */
    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * Gets country.
     *
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets country.
     *
     * @param country the country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Gets loc.
     *
     * @return the loc
     */
    public String getLoc() {
        return loc;
    }

    /**
     * Sets loc.
     *
     * @param loc the loc
     */
    public void setLoc(String loc) {
        this.loc = loc;
    }

    /**
     * Gets org.
     *
     * @return the org
     */
    public String getOrg() {
        return org;
    }

    /**
     * Sets org.
     *
     * @param org the org
     */
    public void setOrg(String org) {
        this.org = org;
    }

    /**
     * Gets postal.
     *
     * @return the postal
     */
    public String getPostal() {
        return postal;
    }

    /**
     * Sets postal.
     *
     * @param postal the postal
     */
    public void setPostal(String postal) {
        this.postal = postal;
    }
}
