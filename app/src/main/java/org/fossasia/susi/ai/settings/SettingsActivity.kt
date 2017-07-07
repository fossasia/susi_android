package org.fossasia.susi.ai.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.helper.Constant

/**
 * Created by mayanktripathi on 07/07/17.
 */

class SettingsActivity: AppCompatActivity(), ISettingsView {

    private val TAG = "SettingsActivity"
    var settingsPresenter: ISettingsPresenter? = null
    var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences(Constant.THEME, Context.MODE_PRIVATE)
        Log.d(TAG, "onCreate: " + prefs?.getString(Constant.THEME, "Light"))
        if (prefs?.getString(Constant.THEME, "Light") == "Dark") {
            setTheme(R.style.PreferencesThemeDark)
        } else {
            setTheme(R.style.PreferencesThemeLight)
        }
        setContentView(R.layout.activity_settings)

    }


}
