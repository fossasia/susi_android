package org.fossasia.susi.ai.skills.settings.contract

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

    fun sendSetting(key: String, value: String, count: Int)

    fun getAnonymity(): Boolean

    fun setServer(isCustomServerChecked: Boolean, url: String)

}
