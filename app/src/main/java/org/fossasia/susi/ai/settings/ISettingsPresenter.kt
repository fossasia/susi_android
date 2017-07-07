package org.fossasia.susi.ai.settings

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by mayanktripathi on 07/07/17.
 */
interface ISettingsPresenter {

    fun onAttach(settingView: ISettingsView, preferences: SharedPreferences)

    fun deleteMsg()

    fun getThemes(): String?

    fun setTheme(string: String)

    fun enableMic(context: Context): Boolean

}