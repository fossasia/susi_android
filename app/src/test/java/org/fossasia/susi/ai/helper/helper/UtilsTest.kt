package org.fossasia.susi.ai.helper.helper

import android.util.Patterns
import junit.framework.Assert.assertTrue
import org.fossasia.susi.ai.helper.Utils
import org.fossasia.susi.ai.rest.responses.susi.SkillData
import org.junit.Test

class UtilsTest {

    val skillData: SkillData = SkillData()

    @Test
    fun verifyImageLink() {
        val str = Utils.getImageLink(skillData)
        val yes = Patterns.WEB_URL.matcher(str).matches()
        assertTrue(yes)
    }
}