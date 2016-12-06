package org.fossasia.susi.ai.rest.model;

import com.google.gson.annotations.SerializedName;

/**
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


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getPostal() {
        return postal;
    }

    public void setPostal(String postal) {
        this.postal = postal;
    }
}
