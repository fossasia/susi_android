package org.fossasia.susi.ai.helper

import org.fossasia.susi.ai.rest.responses.susi.SkillData
import org.junit.Assert.assertEquals
import org.junit.Test

class UtilsTest {
    @Test
    fun testTruncateEmailAtEnd() {
        assertEquals("testuser@ ...", Utils.truncateEmailAtEnd("testuser@example.com"))
        assertEquals(null, Utils.truncateEmailAtEnd("testuser"))
        assertEquals(null, Utils.truncateEmailAtEnd(""))
        assertEquals(null, Utils.truncateEmailAtEnd(" "))
        assertEquals(null, Utils.truncateEmailAtEnd(null))
        assertEquals("test.user@ ...", Utils.truncateEmailAtEnd("test.user@example.com"))
        assertEquals("test_user@ ...", Utils.truncateEmailAtEnd("test_user@example.com"))
        assertEquals("test123@ ...", Utils.truncateEmailAtEnd("test123@example.com"))
    }

    @Test
    fun verifyImageLink() {
        assertEquals("https://api.susi.ai/cms/getImage.png?model=model&language=test&group=abc%20def%20ghi&image=images/test.png",
                Utils.getImageLink(SkillData(model = "model", group = "abc def ghi", language = "test", image = "images/test.png")))

        assertEquals("https://api.susi.ai/cms/getImage.png?model=model&language=test&group=abcdefghi&image=images/test.png",
                Utils.getImageLink(SkillData(model = "model", group = "abcdefghi", language = "test", image = "images/test.png")))
    }
}
