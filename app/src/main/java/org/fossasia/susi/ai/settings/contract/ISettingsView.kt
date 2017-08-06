package org.fossasia.susi.ai.settings.contract

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
    fun onResetPasswordResponse(message: String)

}