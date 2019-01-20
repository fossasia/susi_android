package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.SerializedName

data class Metrics(
    val staffPicks: List<SkillData> = ArrayList(),
    val feedback: List<SkillData> = ArrayList(),
    val usage: List<SkillData> = ArrayList(),
    val rating: List<SkillData> = ArrayList(),
    val latest: List<SkillData> = ArrayList(),
    val newest: List<SkillData> = ArrayList(),
    @SerializedName("Games, Trivia and Accessories")
    val topGames: List<SkillData> = ArrayList()
)
