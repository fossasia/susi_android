package org.fossasia.susi.ai.skills.settings

import android.content.Context
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.data.UtilModel
import org.fossasia.susi.ai.data.contract.ISettingModel
import org.fossasia.susi.ai.data.db.DatabaseRepository
import org.fossasia.susi.ai.data.db.contract.IDatabaseRepository
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.CredentialHelper
import org.fossasia.susi.ai.rest.responses.susi.ChangeSettingResponse
import org.fossasia.susi.ai.rest.responses.susi.ResetPasswordResponse
import org.fossasia.susi.ai.skills.settings.contract.ISettingsPresenter
import org.fossasia.susi.ai.skills.settings.contract.ISettingsView
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf
import retrofit2.Response

/**
 * Presenter for Settings
 * The P in MVP
 *
 * Created by mayanktripathi on 07/07/17.
 */

class SettingsPresenter(context: Context, private val settingView: ISettingsView?) :
    ISettingsPresenter, ISettingModel.OnSettingFinishListener, KoinComponent {

    private val settingModel: ISettingModel by inject { parametersOf(this) }
    private var utilModel: UtilModel = UtilModel(context)
    private var databaseRepository: IDatabaseRepository = DatabaseRepository()

    override fun enableMic(): Boolean {
        return if ((settingView?.micPermission()) as Boolean) {
            if (!utilModel.checkMicInput())
                utilModel.putBooleanPref(R.string.setting_mic_key, false)
            utilModel.checkMicInput()
        } else {
            utilModel.putBooleanPref(R.string.setting_mic_key, false)
            false
        }
    }

    override fun enableHotword(): Boolean {
        return if (settingView?.hotWordPermission() as Boolean) {
            return if (utilModel.checkMicInput() && utilModel.isArmDevice()) {
                true
            } else {
                utilModel.putBooleanPref(R.string.hotword_detection_key, false)
                false
            }
        } else {
            utilModel.putBooleanPref(R.string.hotword_detection_key, false)
            false
        }
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
        settingModel.resetPassword(password, newPassword)
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

    override fun sendSetting(key: String, value: String, count: Int) {
        settingModel.sendSetting(key, value, count)
    }

    override fun checkForPassword(password: String, what: String) {
        if (!CredentialHelper.isPasswordValid(password))
            settingView?.passwordInvalid(what)
    }

    override fun getAnonymity(): Boolean {
        return utilModel.getAnonymity()
    }

    override fun setServer(isCustomServerChecked: Boolean, url: String) {
        if (isCustomServerChecked) {
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
            utilModel.putBooleanPref(R.string.susi_server_selected_key, true)
        }
        settingView?.setServerSuccessful()
    }
}
