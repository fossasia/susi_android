package org.fossasia.susi.ai.helper;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import org.fossasia.susi.ai.MainApplication;
import org.fossasia.susi.ai.rest.BaseUrl;
import org.fossasia.susi.ai.rest.model.SusiBaseUrls;

import java.util.Set;

/**
 * @author Rajan Maurya
 */
public class PrefManager {

    private static final String SUSI_BASE_URLS = "preferences_base_urls";
    private static final String SUSI_RUNNING_BASE_URL = "preferences_running_base_url";

    private static Gson gson = new Gson();

    private static SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(MainApplication.getInstance()
                .getApplicationContext());
    }

    public static void clearPrefs() {
        getPreferences().edit().clear().apply();
    }

    public static int getInt(String preferenceKey, int preferenceDefaultValue) {
        return getPreferences().getInt(preferenceKey, preferenceDefaultValue);
    }

    public static void putInt(String preferenceKey, int preferenceValue) {
        getPreferences().edit().putInt(preferenceKey, preferenceValue).apply();
    }

    public static long getLong(String preferenceKey, long preferenceDefaultValue) {
        return getPreferences().getLong(preferenceKey, preferenceDefaultValue);
    }

    public static void putLong(String preferenceKey, long preferenceValue) {
        getPreferences().edit().putLong(preferenceKey, preferenceValue).apply();
    }

    public static float getFloat(String preferenceKey, float preferenceDefaultValue) {
        return getPreferences().getFloat(preferenceKey, preferenceDefaultValue);
    }

    public static void putFloat(String preferenceKey, float preferenceValue) {
        getPreferences().edit().putFloat(preferenceKey, preferenceValue).apply();
    }

    public static boolean getBoolean(String preferenceKey, boolean preferenceDefaultValue) {
        return getPreferences().getBoolean(preferenceKey, preferenceDefaultValue);
    }

    public static void putBoolean(String preferenceKey, boolean preferenceValue) {
        getPreferences().edit().putBoolean(preferenceKey, preferenceValue).apply();
    }

    public static String getString(String preferenceKey, String preferenceDefaultValue) {
        return getPreferences().getString(preferenceKey, preferenceDefaultValue);
    }

    public static void putString(String preferenceKey, String preferenceValue) {
        getPreferences().edit().putString(preferenceKey, preferenceValue).apply();
    }

    public static void putStringSet(String preferencesKey, Set<String> values) {
        getPreferences().edit().putStringSet(preferencesKey, values).apply();
    }

    public static Set<String> getStringSet(String preferencesKey) {
        return getPreferences().getStringSet(preferencesKey, null);
    }

    /**
     * This method for retrieving current Susi base url from SharedPreferences
     * @return String Running Susi Base Url
     */
    public static String getSusiRunningBaseUrl(){

        if(getBoolean("is_susi_server_selected", true)){
            return getString(SUSI_RUNNING_BASE_URL, BaseUrl.SUSI_DEFAULT_BASE_URL);
        }
        return getString("custom_server", "null");
    }

    /**
     * This method will give current susi running base url.
     *
     * @param runningBaseUrl Running Base ur;
     */
    public static void setSusiRunningBaseUrl(String runningBaseUrl) {
        getPreferences().edit().putString(SUSI_RUNNING_BASE_URL, runningBaseUrl).apply();
    }

    /**
     * This Method will save the Susi base urls that will be fetched from server.
     * @param susiBaseUrls Susi All base urls
     */
    public static void saveBaseUrls(SusiBaseUrls susiBaseUrls) {
        putString(SUSI_BASE_URLS, gson.toJson(susiBaseUrls));
    }

    /**
     * This Method for retrieving All Susi base url from SharedPreferences
     * @return SusiBaseUrls
     */
    public static SusiBaseUrls getBaseUrls() {
        return gson.fromJson(getString(SUSI_BASE_URLS, "null"), SusiBaseUrls.class);
    }


    public static boolean hasTokenExpired() {
        long validTime = getLong(Constant.TOKEN_VALIDITY, 0);
        return validTime < System.currentTimeMillis();
    }

    public static String getToken() {
        return hasTokenExpired() ? null : getString(Constant.ACCESS_TOKEN, null);
    }

    public static void clearToken() {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.remove(Constant.ACCESS_TOKEN);
        editor.remove(Constant.TOKEN_VALIDITY);
        editor.apply();
    }
}


