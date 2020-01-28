package org.fossasia.susi.ai.helper

import org.fossasia.susi.ai.rest.responses.susi.SkillData

object ListHelper {
    fun revertList(list: List<SkillData>): List<SkillData> {
        return list.reversed()
    }
}
