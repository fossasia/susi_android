package org.fossasia.susi.ai.helper

import android.support.test.runner.AndroidJUnit4
import android.util.Patterns
import junit.framework.Assert.assertTrue
import org.fossasia.susi.ai.rest.responses.susi.SkillData
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UtilsTest {

    lateinit var skillData: SkillData

    @Before
    fun setUp() {
        skillData = SkillData()
    }

    @Test
    fun verifyImageLink() {
        val str = Utils.getImageLink(skillData)
        val yes = Patterns.WEB_URL.matcher(str).matches()
        assertTrue(yes)
    }
}