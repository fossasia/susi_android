package org.fossasia.susi.ai.rest.responses.susi

/**
 *
 * Created by arundhati24 on 12/07/2018
 */
data class Metrics (
    val feedback: List<SkillData> = ArrayList(),
    val usage: List<SkillData> = ArrayList(),
    val rating: List<SkillData> = ArrayList(),
    val latest: List<SkillData> = ArrayList()
)