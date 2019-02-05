package org.fossasia.susi.ai.dataclasses

import org.fossasia.susi.ai.rest.responses.susi.SkillData

/**
 *
 * Created by arundhati24 on 16/07/2018.
 */
data class GroupWiseSkills(
    var group: String,
    var skillsList: MutableList<SkillData>
)