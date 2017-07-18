package org.fossasia.susi.ai.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import io.realm.Realm
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

class UtilModel(val context: Context): IUtilModel {

    override fun saveToken(response: Response<LoginResponse>) {
        PrefManager.putString(Constant.ACCESS_TOKEN, response.body().accessToken as String)
        val validity = System.currentTimeMillis() + response.body().validSeconds * 1000
        PrefManager.putLong(Constant.TOKEN_VALIDITY, validity)
    }

    override fun deleteAllMessages() {
        val realm: Realm = Realm.getDefaultInstance()
        realm.executeTransaction({ realm ->
            realm.deleteAll()
        })
    }

    fun getTheme(): String {
        return PrefManager.getTheme()
    }

    fun setTheme(string: String) {
        PrefManager.putTheme(Constant.THEME, string)
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

    fun setEnableMic(): Boolean {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            val voiceInputAvailable = MediaUtil.isAvailableForVoiceInput(context)
            if (!voiceInputAvailable)
                PrefManager.putBoolean(Constant.MIC_INPUT, false)
            return voiceInputAvailable
        } else {
            PrefManager.putBoolean(Constant.MIC_INPUT, false)
            return false
        }
    }

    fun setEnableHotword(): Boolean {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            val voiceInputAvailable = MediaUtil.isAvailableForVoiceInput(context)
            if (!voiceInputAvailable || !Build.CPU_ABI.contains("arm") || Build.FINGERPRINT.contains("generic")) {
                PrefManager.putBoolean(Constant.HOTWORD_DETECTION, false)
                return false
            } else {
                return true
            }
        } else {
            PrefManager.putBoolean(Constant.HOTWORD_DETECTION, false)
            return false
        }
    }
}
