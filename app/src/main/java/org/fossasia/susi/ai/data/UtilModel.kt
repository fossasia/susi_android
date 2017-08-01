package org.fossasia.susi.ai.data

import android.Manifest
import ai.kitt.snowboy.AppResCopy
import android.content.Context
import android.os.Build
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import org.fossasia.susi.ai.data.contract.IUtilModel
import org.fossasia.susi.ai.helper.*
import org.fossasia.susi.ai.rest.responses.susi.LoginResponse
import retrofit2.Response

/**
 * Util Model class used for utilities like preferences, etc
 *
 * Created by chiragw15 on 10/7/17.
 */

class UtilModel(val context: Context): IUtilModel {

    override fun saveToken(response: Response<LoginResponse>) {
        PrefManager.putString(Constant.ACCESS_TOKEN, response.body().accessToken as String)
        val validity = System.currentTimeMillis() + response.body().validSeconds * 1000
        PrefManager.putLong(Constant.TOKEN_VALIDITY, validity)
    }

    override fun saveAnonymity(isAnonymous: Boolean) {
        PrefManager.putBoolean(Constant.ANONYMOUS_LOGGED_IN, isAnonymous)
    }

    override fun getAnonymity(): Boolean{
        return PrefManager.getBoolean(Constant.ANONYMOUS_LOGGED_IN, false)
    }

    override fun saveEmail(email: String) {
        val savedEmails = mutableSetOf<String>()
        if(PrefManager.getStringSet(Constant.SAVED_EMAIL) != null)
            savedEmails.addAll(PrefManager.getStringSet(Constant.SAVED_EMAIL))
        savedEmails.add(email)
        PrefManager.putStringSet(Constant.SAVED_EMAIL, savedEmails)
    }

    override fun getSavedEmails(): MutableSet<String>? {
        return PrefManager.getStringSet(Constant.SAVED_EMAIL)
    }

    override fun isLoggedIn(): Boolean {
        return !PrefManager.hasTokenExpired()
    }

    override fun clearToken() {
        PrefManager.clearToken()
    }

    override fun setServer(isSusiServer: Boolean) {
        PrefManager.putBoolean(Constant.SUSI_SERVER, isSusiServer)
    }

    override fun setCustomURL(url: String) {
        PrefManager.putString(Constant.CUSTOM_SERVER, CredentialHelper.getValidURL(url) as String)
    }

    override fun getString(id: Int): String {
        return context.getString(id)
    }

    override fun getBooleanPref(prefName: String, defaultValue: Boolean): Boolean {
        return PrefManager.getBoolean(prefName, defaultValue);
    }

    override fun putBooleanPref(prefName: String, value: Boolean) {
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
}
