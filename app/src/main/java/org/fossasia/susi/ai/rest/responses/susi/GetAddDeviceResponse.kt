package org.fossasia.susi.ai.rest.responses.susi

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GetAddDeviceResponse(
    val session: Session? = null,
    val accepted: Boolean = false,
    val message: String? = null
) : Parcelable
