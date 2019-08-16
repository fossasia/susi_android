package org.fossasia.susi.ai.signup

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.fossasia.susi.ai.helper.CredentialHelper
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber

@RunWith(AndroidJUnit4::class)
class SignUpActivityInstrumentationTest {

    @Rule
    @JvmField
    val activityRule = ActivityTestRule(SignUpActivity::class.java)

    @Test
    fun testSignupPassword() {
        Timber.d("running password test..")
        val password = "123345602"
        val result = CredentialHelper.isPasswordValid(password)
        Assert.assertEquals("Pattern did not match", false, result)
    }
}