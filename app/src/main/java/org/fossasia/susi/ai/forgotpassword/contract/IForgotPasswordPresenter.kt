package org.fossasia.susi.ai.forgotpassword.contract

/**
 * The interface for ForgotPassword Presenter
 *
 * Created by meeera on 7/7/17.
 */
interface IForgotPasswordPresenter {

    fun onAttach(forgotPasswordView: IForgotPasswordView)
    fun requestPassword(email: String, url: String, isPersonalServerChecked: Boolean)
    fun cancelSignup()
    fun onDetach()
}