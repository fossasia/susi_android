package org.fossasia.susi.ai.skills.skillactivity

import org.fossasia.susi.ai.rest.responses.susi.SkillData

interface SkillFragmentCallback {
     fun loadSkillDetailFragment(skillTag: String, skillData: SkillData, skillGroup: String)
     fun  startChatActivity(position: Int, examples: List<String>)
}
