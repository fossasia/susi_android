package org.fossasia.susi.ai.settings

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import io.realm.Realm
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.MediaUtil
import org.fossasia.susi.ai.helper.PrefManager

/**
 * Created by mayanktripathi on 07/07/17.
 */

class SettingsInteractor: ISettingsInteractor{

    override fun getTheme(preferences: SharedPreferences): String {
        return preferences.getString(Constant.THEME, "Light")!!
    }

    override fun setTheme(string: String, preferences: SharedPreferences) {
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString(Constant.THEME, string)
        editor.apply()
    }

    override fun deleteMsg() {
        PrefManager.putBoolean(Constant.SUSI_SERVER, true)
        PrefManager.clearToken()
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction { realm -> realm.deleteAll() }
    }

    override fun setEnableMic(context: Context): Boolean {
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

}
