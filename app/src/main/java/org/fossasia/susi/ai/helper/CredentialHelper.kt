package org.fossasia.susi.ai.helper

import android.content.Context
import android.support.design.widget.TextInputLayout
import android.text.TextUtils
import android.util.Log
import android.util.Patterns

import org.fossasia.susi.ai.R

import java.net.URL
import java.util.regex.Pattern

/**
 * <h1>Helper class to verify credentials of user during login and sign up.</h1>

 * Created by saurabh on 11/10/16.
 */
object CredentialHelper {

    private val TAG = "CredentialHelper"

    private val PASSWORD_PATTERN = Pattern.compile("^.{6,64}$")

    /**
     * Is email valid boolean.

     * @param mail the email
     * *
     * @return the boolean
     */
    fun isEmailValid(mail: String): Boolean {
        Log.d(TAG, "isEmailValid: " + mail)
        val email = mail.trim { it <= ' ' }
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Is password valid boolean.

     * @param password the password
     * *
     * @return the boolean
     */
    fun isPasswordValid(password: String): Boolean {
        return PASSWORD_PATTERN.matcher(password).matches()
    }

    /**
     * Clear fields.

     * @param layouts the layouts
     */
    fun clearFields(vararg layouts: TextInputLayout) {
        for (inputLayout in layouts) {
            if (inputLayout.editText != null) {
                inputLayout.editText!!.text = null
            }
            inputLayout.error = null
        }
    }

    /**
     * Is url valid boolean.

     * @param url     the url
     * *
     * @return the boolean
     */
    fun isURLValid(url: String): Boolean {
        return Patterns.WEB_URL.matcher(url).matches()
    }

    fun isURLValid(inputLayout: TextInputLayout, context: Context): Boolean {
        if(Patterns.WEB_URL.matcher(inputLayout.editText?.text.toString()).matches()) {
            inputLayout.error = null
            return true
        } else {
            inputLayout.error = context.getString(R.string.invalid_url)
            return false
        }
    }
    /**
     * Gets valid url.

     * @param url     the url
     * *
     * @return the valid url
     */
    fun getValidURL(url: String): String? {
        try {
            if (url.trim { it <= ' ' }.substring(0, 7) == "http://" || url.trim { it <= ' ' }.substring(0, 8) == "https://") {
                val susiURL = URL(url.trim { it <= ' ' })
                return susiURL.protocol + "://" + susiURL.host
            } else {
                val susiURL = URL("http://" + url.trim { it <= ' ' })
                return susiURL.protocol + "://" + susiURL.host
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

    /**
     * Check if empty boolean.

     * @param inputLayout the input layout
     * *
     * @param context     the context
     * *
     * @return the boolean
     */
    fun checkIfEmpty(inputLayout: TextInputLayout, context: Context): Boolean {
        if (TextUtils.isEmpty(inputLayout.editText!!.text.toString())) {
            inputLayout.error = context.getString(R.string.field_cannot_be_empty)
            return true
        } else {
            inputLayout.error = null
            return false
        }
    }
}
