package org.fossasia.susi.ai.rest.responses.susi

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.ArrayList

@Parcelize
data class SusiBaseUrls(
    @SerializedName("susi_service")
    var susiServices: List<String> = ArrayList()
) : Parcelable