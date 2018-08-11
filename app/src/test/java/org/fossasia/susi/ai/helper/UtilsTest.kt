package org.fossasia.susi.ai.helper

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.fossasia.susi.ai.rest.responses.susi.SkillData
import org.junit.Before
import org.junit.Test

class UtilsTest {

    @Test
    fun verifyImageLink() {
        assertEquals("https://api.susi.ai/cms/getImage.png?model=model&language=test&group=abc%20def%20ghi&image=images/test.png",
                Utils.getImageLink(SkillData(model = "model", group = "abc def ghi", language = "test", image = "images/test.png")))

        assertEquals("https://api.susi.ai/cms/getImage.png?model=model&language=test&group=abcdefghi&image=images/test.png",
                Utils.getImageLink(SkillData(model = "model", group = "abcdefghi", language = "test", image = "images/test.png")))
    }
}
