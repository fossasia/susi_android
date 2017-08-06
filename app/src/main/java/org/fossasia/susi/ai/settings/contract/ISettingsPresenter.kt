package org.fossasia.susi.ai.settings.contract

/**
 * The interface for Settings Presenter
 *
 * Created by mayanktripathi on 07/07/17.
 */

interface ISettingsPresenter {

    fun enableMic(): Boolean

    fun enableHotword(): Boolean

    fun onAttach(settingView: ISettingsView)

    fun onDetach()

    fun loginLogout()

    fun resetPassword(password: String, newPassword: String, conPassword: String)

    fun checkForPassword(password: String, what: String)

    fun sendSetting(key: String, value: String)

    fun getAnonymity(): Boolean

}
