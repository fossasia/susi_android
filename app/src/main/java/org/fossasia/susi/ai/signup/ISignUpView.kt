package org.fossasia.susi.ai.signup

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
    fun clearField()
    fun showProgress()
    fun hideProgress()
    fun passwordInvalid()
    fun emptyEmailError()
    fun emptyPasswordError()
    fun emptyConPassError()

}