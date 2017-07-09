package org.fossasia.susi.ai.forgotPassword

import android.content.Context

/**
 * Created by meeera on 6/7/17.
 */
class ForgotPasswordPresenter : IForgotPasswordPresenter, IForgotPasswordInteractor.onFinishListener {

    var forgotPasswordInteractor: ForgotPasswordInteractor ?= null
    var forgotPasswordView: IForgotPasswordView?= null

    override fun onAttach(forgotPasswordView: IForgotPasswordView) {
        this.forgotPasswordView = forgotPasswordView
        this.forgotPasswordInteractor = ForgotPasswordInteractor()
    }

    override fun emptyEmail() {
        forgotPasswordView?.emptyEmail()
    }

    override fun wrongEmail() {
        forgotPasswordView?.wrongEmail()
    }

    override fun invalidUrl() {
        forgotPasswordView?.invalidUrl()
    }

    override fun emptyUrl() {
        forgotPasswordView?.emptyUrl()
    }

    override fun showProgress() {
        forgotPasswordView?.showProgress()
    }

    override fun hideProgress() {
        forgotPasswordView?.hideProgress()
    }

    override fun success(title: String, message: String) {
        forgotPasswordView?.success(title, message)
    }

    override fun failure(title: String, message: String, button: String, color: Int) {
        forgotPasswordView?.failure(title, message, button, color)
    }

    override fun cancelSignup() {
        forgotPasswordInteractor?.cancelSignup()
    }

    override fun signup(email: String, url: String, isPersonalServerChecked: Boolean, context: Context) {
        forgotPasswordInteractor?.signup(email, url, isPersonalServerChecked, context, this)
    }

    override fun onDetach() {
        forgotPasswordView = null
    }
}