package org.fossasia.susi.ai.data.contract

import android.graphics.drawable.Drawable
import android.speech.SpeechRecognizer
import org.fossasia.susi.ai.rest.responses.susi.LoginResponse
import retrofit2.Response

/**
 * Interface for Util Model
 *
 * Created by chiragw15 on 10/7/17.
 */
interface IUtilModel {

    fun saveToken(response: Response<LoginResponse>)
    fun saveAnonymity(isAnonymous: Boolean)
    fun getAnonymity(): Boolean
    fun saveEmail(email: String)
    fun getSavedEmails(): MutableSet<String>?
    fun isLoggedIn(): Boolean
    fun clearToken()
    fun setServer(isSusiServer: Boolean)
    fun setCustomURL(url: String)
    fun getString(id: Int): String
    fun getBooleanPref(prefName: String, defaultValue: Boolean): Boolean
    fun putBooleanPref(prefName: String, value: Boolean)
    fun checkMicInput(): Boolean
    fun copyAssetstoSD()
    fun permissionsToGet() : Array<String>
    fun isArmDevice(): Boolean
}