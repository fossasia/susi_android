package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 *
 * Created by arundhati24 on 12/07/2018
 */
class Metrics {
    val feedback: List<SkillData> = ArrayList()
    val usage: List<SkillData> = ArrayList()
    val rating: List<SkillData> = ArrayList()
    val latest: List<SkillData> = ArrayList()
    val newest: List<SkillData> = ArrayList()

    @SerializedName("Games, Trivia and Accessories")
    @Expose
    val topGames: List<SkillData> = ArrayList()
}