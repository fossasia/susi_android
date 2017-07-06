package org.fossasia.susi.ai.signup

import android.app.ProgressDialog
import android.content.Context

/**
 * Created by mayanktripathi on 05/07/17.
 */

class SignUpPresenter: ISignUpPresenter, ISignUpInteractor.OnLoginFinishedListener {

    var signUpView: ISignUpView? = null
    var signUpInteractor: SignUpInteractor? = null
    var context: Context? = null

    override fun onAttach(signUpView: ISignUpView) {
        this.signUpView = signUpView
        this.signUpInteractor = SignUpInteractor()
    }

    override fun signUp(email: String, password: String, conpass: String, url: String) {
        signUpInteractor?.signUp(email, password, conpass, url, this)
    }

    override fun enableSignUp(b: Boolean) {
        signUpView?.enableSignUp(b)
    }

    override fun alertError(string: String) {
        signUpView?.alertError(string)
    }

    override fun alertFailure() {
        signUpView?.alertFailure()
    }

    override fun clearField() {
        signUpView?.clearField()
    }

    override fun alertSuccess() {
        signUpView?.alertSuccess()
    }

    override fun showProcess(): ProgressDialog {
        return signUpView?.showProcess()!!
    }

    override fun setErrorEmail() {
        signUpView?.setErrorEmail()
    }

    override fun passwordInvalid() {
        signUpView?.passwordInvalid()!!
    }

    override fun setErrorPass() {
        signUpView?.setErrorPass()
    }

    override fun isPersonalServer(): Boolean {
        return signUpView?.isPersonalServer() as Boolean
    }

    override fun setErrorUrl() {
        signUpView?.setErrorUrl()
    }

    override fun emptyEmail() {
        signUpView?.emptyEmailError()
    }

    override fun emptyPassword() {
        signUpView?.emptyPasswordError()
    }

    override fun emptyConPassword() {
        signUpView?.emptyConPassError()
    }

    override fun onDetach() {
        signUpView = null
    }
}