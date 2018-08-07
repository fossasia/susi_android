package org.fossasia.susi.ai.rest.responses.susi

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * <h1>Kotlin Data class to parse identity object in retrofit response from susi client.</h1>
 */
@Parcelize
data class Identity(
        val name: String? = null,
        val type: String? = null,
        val anonymous: Boolean = false
) : Parcelable
