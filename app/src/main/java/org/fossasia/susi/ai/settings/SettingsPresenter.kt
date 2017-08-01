package org.fossasia.susi.ai.settings

import android.support.v4.app.FragmentActivity
import org.fossasia.susi.ai.data.UtilModel
import org.fossasia.susi.ai.data.db.DatabaseRepository
import org.fossasia.susi.ai.data.db.contract.IDatabaseRepository
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
    var databaseRepository: IDatabaseRepository = DatabaseRepository()

    override fun onAttach(chatSettingsFragment: ChatSettingsFragment) {
        this.settingView = settingView
    }

    override fun deleteMsg() {
        utilModel.deleteAllMessages()
    }

    override fun enableMic(): Boolean {
        return utilModel.setEnableMic()
    }

    override fun enableHotword(): Boolean {
        return utilModel.setEnableHotword()
    }

    override fun getAnonymity(): Boolean {
        return utilModel.getAnonymity()
    }

    override fun logout() {
        utilModel.clearToken()
        databaseRepository.deleteAllMessages()
        settingView?.startLoginActivity()
    }

    override fun login() {
        utilModel.clearToken()
        utilModel.saveAnonymity(false)
        databaseRepository.deleteAllMessages()
        settingView?.startLoginActivity()
    }

    override fun onDetach() {
        settingView = null
    }
}
