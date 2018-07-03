package org.fossasia.susi.ai.dataclasses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

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