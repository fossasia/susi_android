package org.fossasia.susi.ai.settings

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by mayanktripathi on 07/07/17.
 */

class SettingsPresenter: ISettingsPresenter{

    var settingView: ISettingsView? = null
    var settingInteractor: SettingsInteractor? = null
    var prefs: SharedPreferences? = null

    override fun onAttach(settingView: ISettingsView, preferences: SharedPreferences) {
        this.settingView = settingView
        this.settingInteractor = SettingsInteractor()
        this.prefs = preferences
    }

    override fun deleteMsg() {
        settingInteractor?.deleteMsg()
    }

    override fun getThemes(): String? {
        return settingInteractor?.getTheme(prefs!!)
    }

    override fun setTheme(string: String) {
        settingInteractor?.setTheme(string, prefs!!)
    }

    override fun enableMic(context: Context): Boolean {
        return settingInteractor?.setEnableMic(context)!!
    }

}
