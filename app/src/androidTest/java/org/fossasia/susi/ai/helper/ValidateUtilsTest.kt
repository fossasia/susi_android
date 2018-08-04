package org.fossasia.susi.ai.helper

import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Test

class ValidateUtilsTest {

    @Test
    fun testValidateUrl() {
        assertTrue(ValidateUtils.validateUrl("https://www.test.com"))
        assertTrue(ValidateUtils.validateUrl("http://www.test.com"))
        assertTrue(ValidateUtils.validateUrl("ftp://www.test.com"))
        assertTrue(ValidateUtils.validateUrl("file://www.test.com"))
        assertTrue(ValidateUtils.validateUrl("https://test.com"))
        assertTrue(ValidateUtils.validateUrl("https://www.TEST.co"))
        assertTrue(ValidateUtils.validateUrl("https://test"))
        assertTrue(ValidateUtils.validateUrl("https://www.TEST.c@!34o"))
        assertTrue(ValidateUtils.validateUrl("https://www.test5465uy.co.in"))
        assertTrue(ValidateUtils.validateUrl("https://www.te/**-st.co.in"))
        assertTrue(ValidateUtils.validateUrl("https://www.test.co.in"))
        assertTrue(ValidateUtils.validateUrl("https://www.test.co"))
        assertTrue(ValidateUtils.validateUrl("https://www.test.in"))
        assertTrue(ValidateUtils.validateUrl("https://54652"))
        assertTrue(ValidateUtils.validateUrl("https://t.co"))
        assertTrue(ValidateUtils.validateUrl("https://tby.test.com"))

        assertFalse(ValidateUtils.validateUrl(""))
        assertFalse(ValidateUtils.validateUrl("ht://www.test.com"))
        assertFalse(ValidateUtils.validateUrl("http/www.test.com"))
        assertFalse(ValidateUtils.validateUrl("http://"))
        assertFalse(ValidateUtils.validateUrl("http://."))
        assertFalse(ValidateUtils.validateUrl("http://...."))
        assertFalse(ValidateUtils.validateUrl("http56://www.test.com"))
        assertFalse(ValidateUtils.validateUrl("http//www.test."))
        assertFalse(ValidateUtils.validateUrl("http//www."))
        assertFalse(ValidateUtils.validateUrl("httpstest.com"))
        assertFalse(ValidateUtils.validateUrl("test"))
        assertFalse(ValidateUtils.validateUrl("test.co.in"))
        assertFalse(ValidateUtils.validateUrl(".com"))
    }
}