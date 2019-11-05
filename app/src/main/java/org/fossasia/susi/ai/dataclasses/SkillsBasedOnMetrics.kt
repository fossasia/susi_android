package org.fossasia.susi.ai.dataclasses

import org.fossasia.susi.ai.rest.responses.susi.SkillData

/**
 *
 * Created by arundhati24 on 12/07/2018.
 */
data class SkillsBasedOnMetrics(
    var metricsList: ArrayList<List<SkillData>?>,
    var metricsGroupTitles: ArrayList<String>,
    var groups: MutableList<String>
)
