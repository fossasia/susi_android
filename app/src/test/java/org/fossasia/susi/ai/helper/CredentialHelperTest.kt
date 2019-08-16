package org.fossasia.susi.ai.helper

import org.junit.Assert
import org.junit.Test
import timber.log.Timber

class CredentialHelperTest {
    @Test
    fun testSignupPassword() {
        Timber.d("running password test..")
        Assert.assertEquals(false, CredentialHelper.isPasswordValid("123345602"))
        Assert.assertEquals(false, CredentialHelper.isPasswordValid("123345602@"))
        Assert.assertEquals(false, CredentialHelper.isPasswordValid("123345602a"))
        Assert.assertEquals(false, CredentialHelper.isPasswordValid("123345602@a"))
        Assert.assertEquals(true, CredentialHelper.isPasswordValid("123345602@As"))
    }
}