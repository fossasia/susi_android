package org.fossasia.susi.ai.settings

import android.content.Context

/**
 * Created by mayanktripathi on 07/07/17.
 */

interface ISettingsPresenter {

    fun onAttach(chatSettingsFragment: ChatSettingsFragment)

    fun deleteMsg()

    fun getThemes(): String?

    fun setTheme(string: String)

    fun enableMic(context: Context): Boolean

    fun onDetach()

}