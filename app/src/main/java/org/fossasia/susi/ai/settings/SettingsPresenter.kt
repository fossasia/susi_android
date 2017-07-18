package org.fossasia.susi.ai.settings

import android.support.v4.app.FragmentActivity
import org.fossasia.susi.ai.data.UtilModel
import org.fossasia.susi.ai.settings.contract.ISettingsPresenter
import org.fossasia.susi.ai.settings.contract.ISettingsView

/**
 * Presenter for Settings
 * The P in MVP
 *
 * Created by mayanktripathi on 07/07/17.
 */

class SettingsPresenter(fragmentActivity: FragmentActivity): ISettingsPresenter {

    var settingView: ISettingsView? = null
    var utilModel: UtilModel = UtilModel(fragmentActivity)

    override fun onAttach(chatSettingsFragment: ChatSettingsFragment) {
        this.settingView = settingView
    }

    override fun deleteMsg() {
        utilModel.deleteAllMessages()
    }

    override fun getThemes(): String? {
        return utilModel.getTheme()
    }

    override fun setTheme(string: String) {
        utilModel.setTheme(string)
    }

    override fun enableMic(): Boolean {
        return utilModel.setEnableMic()
    }

    override fun enableHotword(): Boolean {
        return utilModel.setEnableHotword()
    }

    override fun onDetach() {
        settingView = null
    }

}
