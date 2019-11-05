package org.fossasia.susi.ai.data

import ai.kitt.snowboy.AppResCopy
import android.Manifest
import android.content.Context
import android.os.Build
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.data.contract.IUtilModel
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.CredentialHelper
import org.fossasia.susi.ai.helper.MediaUtil
import org.fossasia.susi.ai.helper.PrefManager
import org.fossasia.susi.ai.rest.responses.susi.LoginResponse
import retrofit2.Response

/**
 * Util Model class used for utilities like preferences, etc
 *
 * Created by chiragw15 on 10/7/17.
 */

class UtilModel(val context: Context) : IUtilModel {
    override fun saveToken(response: Response<LoginResponse>) {
        val loginResponse = response.body()
        if (loginResponse != null) {
            PrefManager.putString(Constant.ACCESS_TOKEN, loginResponse.accessToken)
            val validity = System.currentTimeMillis() + loginResponse.validSeconds * 1000
            PrefManager.putLong(Constant.TOKEN_VALIDITY, validity)
        }
    }

    override fun saveAnonymity(isAnonymous: Boolean) {
        PrefManager.putBoolean(R.string.anonymous_logged_in_key, isAnonymous)
    }

    override fun getAnonymity(): Boolean {
        return PrefManager.getBoolean(R.string.anonymous_logged_in_key, false)
    }

    override fun saveEmail(email: String) {
        val savedEmails = mutableSetOf<String>()
        val storedEmails = PrefManager.getStringSet(Constant.SAVED_EMAIL)
        storedEmails?.let { savedEmails.addAll(it) }
        savedEmails.add(email)
        PrefManager.putString(Constant.SAVE_EMAIL, email)
        PrefManager.putStringSet(Constant.SAVED_EMAIL, savedEmails)
    }

    override fun getSavedEmails(): Set<String>? {
        return PrefManager.getStringSet(Constant.SAVED_EMAIL)
    }

    override fun isLoggedIn(): Boolean {
        return !PrefManager.hasTokenExpired()
    }

    override fun clearToken() {
        PrefManager.clearToken()
    }

    override fun setServer(isSusiServer: Boolean) {
        PrefManager.putBoolean(R.string.susi_server_selected_key, isSusiServer)
    }

    override fun setCustomURL(url: String) {
        PrefManager.putString(Constant.CUSTOM_SERVER, CredentialHelper.getValidURL(url))
    }

    override fun getString(id: Int): String {
        return context.getString(id)
    }

    override fun getBooleanPref(prefName: Int, defaultValue: Boolean): Boolean {
        return PrefManager.getBoolean(prefName, defaultValue)
    }

    override fun putBooleanPref(prefName: Int, value: Boolean) {
        PrefManager.putBoolean(prefName, value)
    }

    override fun checkMicInput(): Boolean {
        return MediaUtil.isAvailableForVoiceInput(context)
    }

    override fun copyAssetstoSD() {
        AppResCopy.copyResFromAssetsToSD(context)
    }

    override fun permissionsToGet(): Array<String> {
        return arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    override fun isArmDevice(): Boolean {
        return Build.CPU_ABI.contains("arm") && !Build.FINGERPRINT.contains("generic")
    }

    override fun setLanguage(language: String) {
        PrefManager.putString(Constant.LANGUAGE, language)
    }

    override fun clearPrefs() {
        PrefManager.clearPrefs()
    }

    override fun putBooleanPref(prefName: String, value: Boolean) {
    }

    override fun getBooleanPref(prefName: String, defaultValue: Boolean): Boolean {
        return false
    }
}
