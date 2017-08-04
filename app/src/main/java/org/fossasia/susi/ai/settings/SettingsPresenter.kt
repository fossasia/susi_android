package org.fossasia.susi.ai.settings

import org.fossasia.susi.ai.data.UtilModel
import org.fossasia.susi.ai.data.db.DatabaseRepository
import org.fossasia.susi.ai.data.db.contract.IDatabaseRepository
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.CredentialHelper
import org.fossasia.susi.ai.settings.contract.ISettingsPresenter
import org.fossasia.susi.ai.settings.contract.ISettingsView

/**
 * Presenter for Settings
 * The P in MVP
 *
 * Created by mayanktripathi on 07/07/17.
 */

class SettingsPresenter(settingsActivity: SettingsActivity): ISettingsPresenter {

    var settingView: ISettingsView? = null
    var utilModel: UtilModel = UtilModel(settingsActivity)
    var databaseRepository: IDatabaseRepository = DatabaseRepository()

    override fun onAttach(settingsView: ISettingsView) {
        this.settingView = settingsView
    }

    override fun enableMic(): Boolean {
        if((settingView?.micPermission()) as Boolean) {
            if(!utilModel.checkMicInput())
                utilModel.putBooleanPref(Constant.MIC_INPUT, false)
            return utilModel.checkMicInput()
        } else {
            utilModel.putBooleanPref(Constant.MIC_INPUT, false)
            return false
        }
    }

    override fun enableHotword(): Boolean {
        if(settingView?.hotWordPermission() as Boolean) {
            if(utilModel.checkMicInput() && utilModel.isArmDevice()) {
                return true
            } else {
                utilModel.putBooleanPref(Constant.HOTWORD_DETECTION, false)
                return false
            }
        } else {
            utilModel.putBooleanPref(Constant.HOTWORD_DETECTION, false)
            return false
        }
    }

    override fun getAnonymity(): Boolean {
        return utilModel.getAnonymity()
    }

    override fun loginLogout() {
        utilModel.clearToken()
        utilModel.saveAnonymity(false)
        databaseRepository.deleteAllMessages()
        settingView?.startLoginActivity()
    }

    override fun resetPassword(password: String, newPassword: String, conPassword: String) {
        if(password.isEmpty()) {
            settingView?.invalidCredentials(true, Constant.PASSWORD)
            return
        }

        if(newPassword.isEmpty()) {
            settingView?.invalidCredentials(true, Constant.NEWPASSWORD)
            return
        }

        if(conPassword.isEmpty()) {
            settingView?.invalidCredentials(true, Constant.CONFIRM_PASSWORD)
            return
        }

        if(!CredentialHelper.isPasswordValid(password)) {
            settingView?.passwordInvalid(Constant.PASSWORD)
            return
        }

        if(!CredentialHelper.isPasswordValid(newPassword)) {
            settingView?.passwordInvalid(Constant.NEWPASSWORD)
            return
        }

        if(newPassword != conPassword) {
            settingView?.invalidCredentials(false, Constant.NEWPASSWORD)
            return
        }
    }

    override fun onDetach() {
        settingView = null
    }
}
