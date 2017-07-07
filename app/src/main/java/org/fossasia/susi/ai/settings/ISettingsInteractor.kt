package org.fossasia.susi.ai.settings

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by mayanktripathi on 07/07/17.
 */
interface ISettingsInteractor{

    fun deleteMsg()

    fun getTheme(preferences: SharedPreferences): String

    fun setTheme(string: String, preferences: SharedPreferences)

    fun setEnableMic(context: Context): Boolean
}