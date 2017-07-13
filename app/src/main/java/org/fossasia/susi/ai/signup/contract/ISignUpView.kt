package org.fossasia.susi.ai.signup.contract

/**
 * The interface for SignUp view
 *
 * Created by mayanktripathi on 05/07/17.
 */

interface ISignUpView {

    fun alertSuccess()
    fun alertFailure()
    fun setErrorConpass(msg: String)
    fun enableSignUp(bool: Boolean)
    fun clearField()
    fun showProgress(bool: Boolean)
    fun passwordInvalid()
    fun invalidCredentials(isEmpty: Boolean, what: String)
    fun onSignUpError(title: String?, message: String?)
}
