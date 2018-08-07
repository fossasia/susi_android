package org.fossasia.susi.ai.rest.responses.susi

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *
 * Created by chiragw15 on 18/8/17.
 */
@Parcelize
data class SkillRating (
    var positive: Int = 0,
    var negative: Int = 0,
    var stars: Stars? = null
) : Parcelable