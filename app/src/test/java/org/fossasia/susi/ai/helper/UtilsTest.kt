package org.fossasia.susi.ai.helper

import junit.framework.Assert.assertTrue
import org.fossasia.susi.ai.rest.responses.susi.SkillData
import org.junit.Before
import org.junit.Test

class UtilsTest {

    private lateinit var skillData: SkillData

    @Test
    fun verifyImageLink() {

        skillData = SkillData()
        val str = Utils.getImageLink(skillData)


    }
}