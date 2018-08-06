package org.fossasia.susi.ai.helper

import android.util.Patterns
import junit.framework.Assert.assertTrue
import org.junit.Test

class UtilsTest {

    @Test
    fun verifyImageLink() {
        val str = Utils.getImageLink()
        val yes = Patterns.WEB_URL.matcher(str).matches()
        assertTrue(yes)
    }
}