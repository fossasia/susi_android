package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Stars : Serializable {

    @SerializedName("one_star")
    @Expose
    var oneStar: String? = null

    @SerializedName("two_star")
    @Expose
    var twoStar: String? = null

    @SerializedName("three_star")
    @Expose
    var threeStar: String? = null

    @SerializedName("four_star")
    @Expose
    var fourStar: String? = null

    @SerializedName("five_star")
    @Expose
    var fiveStar: String? = null

    @SerializedName("total_star")
    @Expose
    var totalStar: String? = null

    @SerializedName("avg_star")
    @Expose
    var averageStar: String? = null
}