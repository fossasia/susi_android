package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 *
 * Created by arundhati24 on 05/06/18.
 */
class Stars : Serializable {

    @SerializedName("one_star")
    @Expose
    var oneStar: Int = 0

    @SerializedName("two_star")
    @Expose
    var twoStar: Int = 0

    @SerializedName("three_star")
    @Expose
    var threeStar: Int = 0

    @SerializedName("four_star")
    @Expose
    var fourStar: Int = 0

    @SerializedName("five_star")
    @Expose
    var fiveStar: Int = 0

    @SerializedName("total_star")
    @Expose
    var totalStar: Int = 0

    @SerializedName("avg_star")
    @Expose
    var averageStar: Float = 0f
}