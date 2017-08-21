package org.fossasia.susi.ai.skills.settings.contract

/**
 * The interface for Settings view
 *
 * Created by mayanktripathi on 07/07/17.
 */

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

}