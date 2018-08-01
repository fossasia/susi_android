package org.fossasia.susi.ai.rest.responses.susi

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * <h1>Kotlin Data class to parse session object in retrofit response from susi client.</h1>
 */
@Parcelize
data class Session(
        val identity: Identity? = null
) : Parcelable
