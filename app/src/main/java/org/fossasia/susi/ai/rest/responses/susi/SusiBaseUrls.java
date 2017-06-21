package org.fossasia.susi.ai.rest.responses.susi;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>POJO class to parse retrofit response from susi client to get susi base urls.</h1>
 *
 * Created by Rajan Maurya on 12/10/16.
 */
public class SusiBaseUrls implements Parcelable {

    @SerializedName("susi_service")
    private List<String> susiServices = new ArrayList<>();

    /**
     * Gets susi services.
     *
     * @return the susi services
     */
    public List<String> getSusiServices() {
        return susiServices;
    }

    /**
     * Sets susi services.
     *
     * @param susiServices the susi services
     */
    public void setSusiServices(List<String> susiServices) {
        this.susiServices = susiServices;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.susiServices);
    }

    /**
     * Instantiates a new Susi base urls.
     */
    public SusiBaseUrls() {
    }

    /**
     * Instantiates a new Susi base urls.
     *
     * @param in the in
     */
    protected SusiBaseUrls(Parcel in) {
        this.susiServices = in.createStringArrayList();
    }

    /**
     * The constant CREATOR.
     */
    public static final Parcelable.Creator<SusiBaseUrls> CREATOR =
            new Parcelable.Creator<SusiBaseUrls>() {
                @Override
                public SusiBaseUrls createFromParcel(Parcel source) {
                    return new SusiBaseUrls(source);
                }

                @Override
                public SusiBaseUrls[] newArray(int size) {
                    return new SusiBaseUrls[size];
                }
            };
}
