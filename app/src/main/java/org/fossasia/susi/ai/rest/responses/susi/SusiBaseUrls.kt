package org.fossasia.susi.ai.rest.responses.susi

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.util.ArrayList
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SusiBaseUrls(
    @SerializedName("susi_service")
    var susiServices: List<String> = ArrayList()
) : Parcelable
