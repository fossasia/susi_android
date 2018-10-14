package org.fossasia.susi.ai.rest.responses.susi

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *
 * @author arundhati24
 */
@Parcelize
data class Feedback(
        var feedback: String? = "",
        var email: String? = "",
        var timestamp: String? = "",
        var avatar: String? = ""
) : Parcelable