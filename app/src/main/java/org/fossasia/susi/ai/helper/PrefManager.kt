package org.fossasia.susi.ai.helper

import android.content.Context
import android.preference.PreferenceManager

import com.google.gson.Gson

import org.fossasia.susi.ai.MainApplication
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.rest.clients.BaseUrl
import org.fossasia.susi.ai.rest.responses.susi.SusiBaseUrls

/**
 * <h1>Helper class to store preferences of user.</h1>
 *
 * @author Rajan Maurya
 */
object PrefManager {

    private const val SUSI_BASE_URLS = "preferences_base_urls"
    private const val SUSI_RUNNING_BASE_URL = "preferences_running_base_url"

    private val gson = Gson()

    private val preferences by lazy { PreferenceManager.getDefaultSharedPreferences(context) }

    /**
     * This method for retrieving current Susi base url from SharedPreferences
     *
     * @return String Running Susi Base Url
     */
    /**
     * This method will give current susi running base url.
     *
     * @param runningBaseUrl Running Base ur;
     */
    var susiRunningBaseUrl: String?
        get() = if (getBoolean(R.string.susi_server_selected_key, true)) {
            getString(SUSI_RUNNING_BASE_URL, BaseUrl.SUSI_DEFAULT_BASE_URL)
        } else getString("custom_server", "null")
        set(runningBaseUrl) = preferences.edit().putString(SUSI_RUNNING_BASE_URL, runningBaseUrl).apply()

    /**
     * This Method for retrieving All Susi base url from SharedPreferences
     *
     * @return SusiBaseUrls base urls
     */
    val baseUrls: SusiBaseUrls?
        get() = gson.fromJson(getString(SUSI_BASE_URLS, "null"), SusiBaseUrls::class.java)

    /**
     * Gets token.
     *
     * @return the token
     */
    val token: String?
        get() = if (hasTokenExpired()) null else getString(Constant.ACCESS_TOKEN, null)

    private val context: Context
        get() = MainApplication.instance.applicationContext

    /**
     * Clear prefs.
     */
    fun clearPrefs() {
        preferences.edit().clear().apply()
    }

    /**
     * Gets int.
     *
     * @param preferenceKey the preference key
     * @param preferenceDefaultValue the preference default value
     * @return the int
     */
    fun getInt(preferenceKey: String, preferenceDefaultValue: Int): Int {
        return preferences.getInt(preferenceKey, preferenceDefaultValue)
    }

    /**
     * Put int.
     *
     * @param preferenceKey the preference key
     * @param preferenceValue the preference value
     */
    fun putInt(preferenceKey: String, preferenceValue: Int) {
        preferences.edit().putInt(preferenceKey, preferenceValue).apply()
    }

    /**
     * Gets long.
     *
     * @param preferenceKey the preference key
     * @param preferenceDefaultValue the preference default value
     * @return the long
     */
    fun getLong(preferenceKey: String, preferenceDefaultValue: Long): Long {
        return preferences.getLong(preferenceKey, preferenceDefaultValue)
    }

    /**
     * Put long.
     *
     * @param preferenceKey the preference key
     * @param preferenceValue the preference value
     */
    fun putLong(preferenceKey: String, preferenceValue: Long) {
        preferences.edit().putLong(preferenceKey, preferenceValue).apply()
    }

    /**
     * Gets float.
     *
     * @param preferenceKey the preference key
     * @param preferenceDefaultValue the preference default value
     * @return the float
     */
    fun getFloat(preferenceKey: String, preferenceDefaultValue: Float): Float {
        return preferences.getFloat(preferenceKey, preferenceDefaultValue)
    }

    /**
     * Put float.
     *
     * @param preferenceKey the preference key
     * @param preferenceValue the preference value
     */
    fun putFloat(preferenceKey: String, preferenceValue: Float) {
        preferences.edit().putFloat(preferenceKey, preferenceValue).apply()
    }

    /**
     * Gets boolean.
     *
     * @param preferenceKey the preference key
     * @param preferenceDefaultValue the preference default value
     * @return the boolean
     */
    fun getBoolean(preferenceKey: Int, preferenceDefaultValue: Boolean): Boolean {
        return preferences.getBoolean(context.getString(preferenceKey), preferenceDefaultValue)
    }

    /**
     * Put boolean.
     *
     * @param preferenceKey the preference key
     * @param preferenceValue the preference value
     */
    fun putBoolean(preferenceKey: Int, preferenceValue: Boolean) {
        preferences.edit().putBoolean(context.getString(preferenceKey), preferenceValue).apply()
    }

    /**
     * Gets string.
     *
     * @param preferenceKey the preference key
     * @param preferenceDefaultValue the preference default value
     * @return the string
     */
    fun getString(preferenceKey: String, preferenceDefaultValue: String?): String {
        return preferences.getString(preferenceKey, preferenceDefaultValue)
    }

    /**
     * Put string.
     *
     * @param preferenceKey the preference key
     * @param preferenceValue the preference value
     */
    fun putString(preferenceKey: String, preferenceValue: String?) {
        preferences.edit().putString(preferenceKey, preferenceValue).apply()
    }

    /**
     * Put string set.
     *
     * @param preferencesKey the preferences key
     * @param values the values
     */
    fun putStringSet(preferencesKey: String, values: Set<String>) {
        preferences.edit().putStringSet(preferencesKey, values).apply()
    }

    /**
     * Gets string set.
     *
     * @param preferencesKey the preferences key
     * @return the string set
     */
    fun getStringSet(preferencesKey: String): Set<String>? {
        return preferences.getStringSet(preferencesKey, null)
    }

    /**
     * This Method will save the Susi base urls that will be fetched from server.
     *
     * @param susiBaseUrls Susi All base urls
     */
    fun saveBaseUrls(susiBaseUrls: SusiBaseUrls) {
        putString(SUSI_BASE_URLS, gson.toJson(susiBaseUrls))
    }

    /**
     * Has token expired boolean.
     *
     * @return the boolean
     */
    fun hasTokenExpired(): Boolean {
        val validTime = getLong(Constant.TOKEN_VALIDITY, 0)
        return validTime < System.currentTimeMillis()
    }

    /**
     * Clear token.
     */
    fun clearToken() {
        val editor = preferences.edit()
        editor.remove(Constant.ACCESS_TOKEN)
        editor.remove(Constant.TOKEN_VALIDITY)
        editor.apply()
    }

    /**
     * Check speech output pref boolean.
     *
     * @return the boolean
     */
    fun checkSpeechOutputPref(): Boolean {
        return PrefManager.getBoolean(R.string.settings_speechPreference_key, true)
    }

    /**
     * Check speech always pref boolean.
     *
     * @return the boolean
     */
    fun checkSpeechAlwaysPref(): Boolean {
        return PrefManager.getBoolean(R.string.settings_speechAlways_key, false)
    }

    /**
     * Check for hotword detection pref boolean
     *
     * @return the boolean
     */
    fun checkHotwordPref(): Boolean {
        return PrefManager.getBoolean(R.string.hotword_detection_key, false)
    }

    /**
     * Check mic input boolean.
     *
     * @return the boolean
     */
    fun checkMicInput(context: Context): Boolean {
        return MediaUtil.isAvailableForVoiceInput(context)
    }
}