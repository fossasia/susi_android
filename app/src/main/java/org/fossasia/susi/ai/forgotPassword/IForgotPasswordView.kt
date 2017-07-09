package org.fossasia.susi.ai.forgotPassword

/**
 * Created by meeera on 6/7/17.
 */
interface IForgotPasswordView {

    fun emptyEmail()
    fun wrongEmail()
    fun invalidUrl()
    fun emptyUrl()
    fun showProgress()
    fun hideProgress()
    fun success(title: String, message: String)
    fun failure(title: String, message: String, button: String, color: Int)
}