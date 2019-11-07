package org.fossasia.susi.ai.rest.responses.susi

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Location(
    val latitude: String? = null,
    val longitude: String? = null
) : Parcelable
