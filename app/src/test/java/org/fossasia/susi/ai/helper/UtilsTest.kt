package org.fossasia.susi.ai.helper

import junit.framework.Assert.assertTrue
import org.fossasia.susi.ai.rest.responses.susi.SkillData
import org.junit.Test

class UtilsTest {

    @Test
    fun verifyImageLink() {
        val str = Utils.getImageLink()
        val yes = ValidateUtils.validateUrl(str)
        assertTrue(yes)
    }
}