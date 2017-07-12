package org.fossasia.susi.ai.settings

import android.content.Context

/**
 * Created by mayanktripathi on 07/07/17.
 */

class SettingsPresenter: ISettingsPresenter{

    var settingView: ISettingsView? = null
    var settingInteractor: SettingsInteractor? = null

    override fun onAttach(chatSettingsFragment: ChatSettingsFragment) {
        this.settingView = settingView
        this.settingInteractor = SettingsInteractor()
    }

    override fun deleteMsg() {
        settingInteractor?.deleteMsg()
    }

    override fun getThemes(): String? {
        return settingInteractor?.getTheme()
    }

    override fun setTheme(string: String) {
        settingInteractor?.setTheme(string)
    }

    override fun enableMic(context: Context): Boolean {
        return settingInteractor?.setEnableMic(context)!!
    }

    override fun onDetach() {
        settingView = null
    }

}
