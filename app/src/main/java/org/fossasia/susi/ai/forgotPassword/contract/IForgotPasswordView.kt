package org.fossasia.susi.ai.forgotPassword.contract

/**
 * The interface for ForgotPassword view
 *
 * Created by meeera on 6/7/17.
 */
interface IForgotPasswordView {

    fun invalidCredentials(isEmpty: Boolean, what: String)
    fun showProgress(boolean: Boolean)
    fun success(title: String?, message: String?)
    fun failure(title: String?, message: String?, button: String?, color: Int)
}