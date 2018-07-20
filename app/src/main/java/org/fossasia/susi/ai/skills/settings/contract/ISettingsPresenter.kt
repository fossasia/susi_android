package org.fossasia.susi.ai.skills.settings.contract

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

    fun checkLogin()

}
