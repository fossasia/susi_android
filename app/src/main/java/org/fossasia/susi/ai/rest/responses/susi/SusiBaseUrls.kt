package org.fossasia.susi.ai.rest.responses.susi

import android.os.Parcel
import android.os.Parcelable
import java.util.*

/**
 * <h1>Kotlin Data class to parse retrofit response from susi client to get susi base urls.</h1>

 * Created by Rajan Maurya on 12/10/16.
 */

class SusiBaseUrls : Parcelable {


    var susiServices: List<String> = ArrayList()

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeStringList(this.susiServices)
    }

    protected constructor(`in`: Parcel) {
        this.susiServices = `in`.createStringArrayList()
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
}
