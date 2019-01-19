package org.fossasia.susi.ai.dataclasses

/**
 *
 * Created by arundhati24 on 08/07/2018
 */
data class SkillsListQuery(
    val group: String = "",
    val language: String = "",
    val applyFilter: String = "",
    val filterName: String = "",
    val filterType: String = ""
)