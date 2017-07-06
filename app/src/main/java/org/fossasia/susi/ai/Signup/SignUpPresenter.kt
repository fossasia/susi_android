package org.fossasia.susi.ai.Signup

import android.app.ProgressDialog
import android.content.Context
import org.fossasia.susi.ai.R


/**
 * Created by mayanktripathi on 05/07/17.
 */

class SignUpPresenter: ISignUpPresenter, ISignUpInteractor.OnLoginFinishedListener {

    var signUpView: ISignUpView? = null
    var signUpInteractor: SignUpInteractor? = null
    var context: Context? = null

    override fun onAttach(signUpView: ISignUpView, context: Context) {
        this.signUpView = signUpView
        this.signUpInteractor = SignUpInteractor()
        this.context = context
    }

    override fun signUp(email: String, password: String, conpass: String) {
        signUpInteractor?.signUp(email, password, conpass, this)
    }

    override fun enableSignUp(b: Boolean) {
        signUpView?.enableSignUp(b)
    }

    override fun checkCredentials(): Boolean? {
        return signUpView?.checkCredentials()
    }

    override fun isEmailValid(email: String): Boolean {
        return signUpView?.isEmailValid(email)!!
    }

    override fun alertError(string: String) {
        signUpView?.alertError(context?.getString(R.string.unknown_host_exception)!!, string)
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

    override fun setErrorEmail(boolean: Boolean) {
        if(boolean) signUpView?.setErrorEmail(context?.getString(R.string.invalid_email)!!)
        else signUpView?.setErrorEmail("")
    }

    override fun checkPasswordValid(): Boolean {
        return signUpView?.checkPasswordValid()!!
    }

    override fun setErrorPass() {
        signUpView?.setErrorPass(context?.getString(R.string.error_password_matching)!!)
    }

    override fun isPersonalServer(): Boolean? {
        return signUpView?.isPersonalServer()
    }

    override fun checkIfEmptyUrl(): Boolean{
        return signUpView?.checkIfEmptyUrl()!!
    }

    override fun isURLValid(): Boolean? {
        return signUpView?.isURLValid()
    }

    override fun getValidURL(): String {
        return signUpView?.getValidURL()!!
    }

    override fun setErrorUrl() {
        signUpView?.setErrorUrl(context?.getString(R.string.invalid_url)!!)
    }

    override fun onDetach() {
        signUpView = null
    }
}