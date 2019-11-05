package org.fossasia.susi.ai.rest.responses.susi

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Stars(
    @SerializedName("one_star")
    var oneStar: Int = 0,
    @SerializedName("two_star")
    var twoStar: Int = 0,
    @SerializedName("three_star")
    var threeStar: Int = 0,
    @SerializedName("four_star")
    var fourStar: Int = 0,
    @SerializedName("five_star")
    var fiveStar: Int = 0,
    @SerializedName("total_star")
    var totalStar: Int = 0,
    @SerializedName("avg_star")
    var averageStar: Float = 0f
) : Parcelable
