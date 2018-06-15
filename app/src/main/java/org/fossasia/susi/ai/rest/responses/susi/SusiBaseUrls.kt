package org.fossasia.susi.ai.rest.responses.susi

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * <h1>Kotlin Data class to parse retrofit response from susi client to get susi base urls.</h1>

 * Created by Rajan Maurya on 12/10/16.
 */

class SusiBaseUrls protected constructor(`in`: Parcel) : Parcelable {

    @SerializedName("susi_service")
    var susiServices: List<String> = ArrayList()

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeStringList(this.susiServices)
    }

    companion object {

        val CREATOR: Parcelable.Creator<SusiBaseUrls> = object : Parcelable.Creator<SusiBaseUrls> {
            override fun createFromParcel(source: Parcel): SusiBaseUrls {
                return SusiBaseUrls(source)
            }

            override fun newArray(size: Int): Array<SusiBaseUrls?> {
                return arrayOfNulls(size)
            }
        }
    }

    init {
        this.susiServices = `in`.createStringArrayList()
    }
}
