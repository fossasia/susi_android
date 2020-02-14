package org.fossasia.susi.ai.helper

import org.junit.Assert
import org.junit.Test

class CredentialHelperTest {
    @Test
    fun testValidPassword() {
        Assert.assertFalse(CredentialHelper.isPasswordValid(""))
        Assert.assertFalse(CredentialHelper.isPasswordValid("123345602"))
        Assert.assertFalse(CredentialHelper.isPasswordValid("123345602@"))
        Assert.assertFalse(CredentialHelper.isPasswordValid("123345602a"))
        Assert.assertFalse(CredentialHelper.isPasswordValid("123345602@a"))
        Assert.assertTrue(CredentialHelper.isPasswordValid("123345602@As"))
    }

    @Test
    fun testValidEmail() {
        Assert.assertFalse(CredentialHelper.isEmailValid(""))
        Assert.assertFalse(CredentialHelper.isEmailValid("email"))
        Assert.assertFalse(CredentialHelper.isEmailValid("email@"))
        Assert.assertFalse(CredentialHelper.isEmailValid("email@."))
        Assert.assertFalse(CredentialHelper.isEmailValid("email@.com"))
        Assert.assertTrue(CredentialHelper.isEmailValid("email@email.com"))
        Assert.assertTrue(CredentialHelper.isEmailValid("www.email@email.com"))
    }

    @Test
    fun testValidUrl() {
        Assert.assertFalse(CredentialHelper.isURLValid(""))
        Assert.assertFalse(CredentialHelper.isURLValid("https"))
        Assert.assertFalse(CredentialHelper.isURLValid("https:"))
        Assert.assertFalse(CredentialHelper.isURLValid("https://"))
        Assert.assertFalse(CredentialHelper.isURLValid("https://www."))
        Assert.assertTrue(CredentialHelper.isURLValid("https://www.google.com"))
        Assert.assertTrue(CredentialHelper.isURLValid("http://www.google.com"))
        Assert.assertTrue(CredentialHelper.isURLValid("http://google.com"))
        Assert.assertTrue(CredentialHelper.isURLValid(CredentialHelper.getValidURL("google.com") as String))
    }

    @Test
    fun testGetValidUrl() {
        Assert.assertEquals(null, CredentialHelper.getValidURL(""))
        Assert.assertEquals(null, CredentialHelper.getValidURL("google"))
        Assert.assertEquals(null, CredentialHelper.getValidURL("google."))
        Assert.assertEquals("http://google.com", CredentialHelper.getValidURL("google.com"))
        Assert.assertEquals("http://www.google.com", CredentialHelper.getValidURL("www.google.com"))
        Assert.assertEquals("https://www.google.com", CredentialHelper.getValidURL("https://www.google.com"))
    }
}
