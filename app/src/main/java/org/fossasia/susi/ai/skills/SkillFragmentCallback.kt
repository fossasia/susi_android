package org.fossasia.susi.ai.skills

import org.fossasia.susi.ai.rest.responses.susi.SkillData

interface SkillFragmentCallback {
     fun loadDetailFragment(skillData: SkillData, skillGroup: String, skillTag: String)
}