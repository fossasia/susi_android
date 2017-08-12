package org.fossasia.susi.ai.settings

import org.fossasia.susi.ai.data.UtilModel
import org.fossasia.susi.ai.data.db.DatabaseRepository
import org.fossasia.susi.ai.data.db.contract.IDatabaseRepository
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.settings.contract.ISettingsPresenter
import org.fossasia.susi.ai.settings.contract.ISettingsView
import org.fossasia.susi.ai.data.SettingModel
import org.fossasia.susi.ai.data.contract.ISettingModel
import org.fossasia.susi.ai.helper.CredentialHelper
import org.fossasia.susi.ai.rest.responses.susi.ChangeSettingResponse
import org.fossasia.susi.ai.rest.responses.susi.ResetPasswordResponse
import retrofit2.Response

/**
 * Presenter for Settings
 * The P in MVP
 *
 * Created by mayanktripathi on 07/07/17.
 */

class SettingsPresenter(settingsActivity: SettingsActivity): ISettingsPresenter, ISettingModel.onSettingFinishListener {

    var settingModel: SettingModel = SettingModel()
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

    override fun loginLogout() {
        utilModel.clearToken()
        utilModel.clearPrefs()
        utilModel.saveAnonymity(false)
        databaseRepository.deleteAllMessages()
        settingView?.startLoginActivity()
    }

    override fun resetPassword(password: String, newPassword: String, conPassword: String) {
        if (password.isEmpty()) {
            settingView?.invalidCredentials(true, Constant.PASSWORD)
            return
        }
        if (newPassword.isEmpty()) {
            settingView?.invalidCredentials(true, Constant.NEW_PASSWORD)
            return
        }
        if (conPassword.isEmpty()) {
            settingView?.invalidCredentials(true, Constant.CONFIRM_PASSWORD)
            return
        }

        if (!CredentialHelper.isPasswordValid(newPassword)) {
            settingView?.passwordInvalid(Constant.NEW_PASSWORD)
            return
        }

        if (newPassword != conPassword) {
            settingView?.invalidCredentials(false, Constant.NEW_PASSWORD)
            return
        }
        settingModel.resetPassword(password,newPassword,this)
    }

    override fun onSuccess(response: Response<ChangeSettingResponse>) {
        settingView?.onSettingResponse(response.message())
    }

    override fun onFailure(throwable: Throwable) {
        settingView?.onSettingResponse(throwable.message.toString())
    }

    override fun onResetPasswordSuccess(response: Response<ResetPasswordResponse>?) {
        settingView?.onResetPasswordResponse(response?.body()?.message.toString())
    }

    override fun onDetach() {
        settingView = null
    }

    override fun sendSetting(key: String, value: String, count: Int) {
        settingModel.sendSetting(key, value, count, this)
    }

    override fun checkForPassword(password: String, what: String) {
        if (!CredentialHelper.isPasswordValid(password))
            settingView?.passwordInvalid(what)
    }

    override fun getAnonymity(): Boolean{
        return utilModel.getAnonymity()
    }

    override fun setServer(isCustomServerChecked: Boolean, url: String) {
        if(isCustomServerChecked) {
            if (url.isEmpty()) {
                settingView?.checkUrl(true)
                return
            }

            if (!CredentialHelper.isURLValid(url)) {
                settingView?.checkUrl(false)
                return
            }

            if (CredentialHelper.getValidURL(url) != null) {
                utilModel.setServer(false)
                utilModel.setCustomURL(CredentialHelper.getValidURL(url).toString())
            } else {
                settingView?.checkUrl(false)
                return
            }
        } else {
            utilModel.putBooleanPref(Constant.SUSI_SERVER, true)
        }
        settingView?.setServerSuccessful()
    }


}
