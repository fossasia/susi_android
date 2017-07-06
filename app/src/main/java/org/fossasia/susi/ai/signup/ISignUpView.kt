package org.fossasia.susi.ai.signup

import android.app.ProgressDialog

/**
 * Created by mayanktripathi on 05/07/17.
 */
interface ISignUpView {

    fun alertSuccess()
    fun alertFailure()
    fun alertError(message: String)
    fun setErrorEmail()
    fun setErrorPass()
    fun setErrorConpass(msg: String)
    fun setErrorUrl()
    fun enableSignUp(bool: Boolean)
    fun isPersonalServer(): Boolean?
    fun clearField()
    fun setupPasswordWatcher()
    fun showProcess(): ProgressDialog
    fun passwordInvalid()
    fun clearFiled()
    fun emptyEmailError()
    fun emptyPasswordError()
    fun emptyConPassError()

}