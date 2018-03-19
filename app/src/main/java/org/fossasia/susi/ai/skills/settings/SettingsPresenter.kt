package org.fossasia.susi.ai.skills.settings

import org.fossasia.susi.ai.data.UtilModel
import org.fossasia.susi.ai.data.db.DatabaseRepository
import org.fossasia.susi.ai.data.db.contract.IDatabaseRepository
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.skills.settings.contract.ISettingsPresenter
import org.fossasia.susi.ai.skills.settings.contract.ISettingsView
import org.fossasia.susi.ai.data.SettingModel
import org.fossasia.susi.ai.data.contract.ISettingModel
import org.fossasia.susi.ai.helper.CredentialHelper
import org.fossasia.susi.ai.rest.responses.susi.ChangeSettingResponse
import org.fossasia.susi.ai.rest.responses.susi.ResetPasswordResponse
import org.fossasia.susi.ai.skills.SkillsActivity

import retrofit2.Response

/**
 * Presenter for Settings
 * The P in MVP
 *
 * Created by mayanktripathi on 07/07/17.
 */

class SettingsPresenter(skillsActivity: SkillsActivity): ISettingsPresenter, ISettingModel.onSettingFinishListener {

    private var settingModel: SettingModel = SettingModel()
    private var settingsView: ISettingsView? = null
    private var utilModel: UtilModel = UtilModel(skillsActivity)
    private var databaseRepository: IDatabaseRepository = DatabaseRepository()

    override fun onAttach(settingsView: ISettingsView) {
        this.settingsView = settingsView
    }

    override fun enableMic(): Boolean {
        if((settingsView?.micPermission()) as Boolean) {
            if(!utilModel.checkMicInput())
                utilModel.putBooleanPref(Constant.MIC_INPUT, false)
            return utilModel.checkMicInput()
        } else {
            utilModel.putBooleanPref(Constant.MIC_INPUT, false)
            return false
        }
    }

    override fun enableHotword(): Boolean {
        if(settingsView?.hotWordPermission() as Boolean) {
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

    override fun loginLogout() {
        utilModel.clearToken()
        utilModel.clearPrefs()
        utilModel.saveAnonymity(false)
        databaseRepository.deleteAllMessages()
        settingsView?.startLoginActivity()
    }

    override fun resetPassword(password: String, newPassword: String, conPassword: String) {
        if (password.isEmpty()) {
            settingsView?.invalidCredentials(true, Constant.PASSWORD)
            return
        }

        if (newPassword.isEmpty()) {
            settingsView?.invalidCredentials(true, Constant.NEW_PASSWORD)
            return
        }

        if (conPassword.isEmpty()) {
            settingsView?.invalidCredentials(true, Constant.CONFIRM_PASSWORD)
            return
        }

        if (!CredentialHelper.isPasswordValid(newPassword)) {
            settingsView?.passwordInvalid(Constant.NEW_PASSWORD)
            return
        }

        if (newPassword != conPassword) {
            settingsView?.invalidCredentials(false, Constant.NEW_PASSWORD)
            return
        }

        settingModel.resetPassword(password,newPassword,this)
    }

    override fun onSuccess(response: Response<ChangeSettingResponse>) {
        settingsView?.onSettingResponse(response.message())
    }

    override fun onFailure(throwable: Throwable) {
        settingsView?.onSettingResponse(throwable.message.toString())
    }

    override fun onResetPasswordSuccess(response: Response<ResetPasswordResponse>?) {
        settingsView?.onResetPasswordResponse(response?.body()?.message.toString())
    }

    override fun sendSetting(key: String, value: String, count: Int) {
        settingModel.sendSetting(key, value, count, this)
    }

    override fun checkForPassword(password: String, what: String) {
        if (!CredentialHelper.isPasswordValid(password))
            settingsView?.passwordInvalid(what)
    }

    override fun getAnonymity(): Boolean{
        return utilModel.getAnonymity()
    }

    override fun setServer(isCustomServerChecked: Boolean, url: String) {
        if(isCustomServerChecked) {
            if (url.isEmpty()) {
                settingsView?.checkUrl(true)
                return
            }

            if (!CredentialHelper.isURLValid(url)) {
                settingsView?.checkUrl(false)
                return
            }

            if (CredentialHelper.getValidURL(url) != null) {
                utilModel.setServer(false)
                utilModel.setCustomURL(CredentialHelper.getValidURL(url).toString())
            } else {
                settingsView?.checkUrl(false)
                return
            }
        } else {
            utilModel.putBooleanPref(Constant.SUSI_SERVER, true)
        }
        settingsView?.setServerSuccessful()
    }

    override fun onDetach() {
        settingsView = null
    }
}
