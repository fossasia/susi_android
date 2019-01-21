package org.fossasia.susi.ai.rest.responses.susi

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Identity(
    val name: String? = null,
    val type: String? = null,
    val anonymous: Boolean = false
) : Parcelable
