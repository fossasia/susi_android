package org.fossasia.susi.ai.rest.responses.susi

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Devices(
    val device: List<Device> = ArrayList()
) : Parcelable