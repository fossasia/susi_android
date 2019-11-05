package org.fossasia.susi.ai.helper

import org.junit.Assert
import org.junit.Test

class CredentialHelperTest {
    @Test
    fun testSignupPassword() {
        Assert.assertFalse(CredentialHelper.isPasswordValid("123345602"))
        Assert.assertFalse(CredentialHelper.isPasswordValid("123345602@"))
        Assert.assertFalse(CredentialHelper.isPasswordValid("123345602a"))
        Assert.assertFalse(CredentialHelper.isPasswordValid("123345602@a"))
        Assert.assertTrue(CredentialHelper.isPasswordValid("123345602@As"))
    }
}
