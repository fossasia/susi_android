package org.fossasia.susi.ai.rest.responses.susi

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Device(
    val name: String? = null,
    val room: String? = null,
    val geolocation: Location? = null
) : Parcelable
