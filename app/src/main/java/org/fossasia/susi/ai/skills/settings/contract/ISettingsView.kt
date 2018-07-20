package org.fossasia.susi.ai.skills.settings.contract

interface ISettingsView {
    fun startLoginActivity()
    fun micPermission(): Boolean
    fun hotWordPermission(): Boolean
    fun onSettingResponse(message: String)
    fun passwordInvalid(what: String)
    fun invalidCredentials(isEmpty: Boolean, what: String)
    fun checkUrl(isEmpty: Boolean)
    fun onResetPasswordResponse(message: String)
    fun setServerSuccessful()
    fun setLoginPreferences(message: String, loginMessage: String)
    fun setLogoutPreferences(message: String, logoutMessage: String)
}