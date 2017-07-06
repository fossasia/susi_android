package org.fossasia.susi.ai.login

import android.content.Context

/**
 * Presenter for Login
 * The P in MVP
 *
 * Created by chiragw15 on 4/7/17.
 */
class LoginPresenter: ILoginPresenter, ILoginInteractor.OnLoginFinishedListener, ILoginInteractor.OnLoginSkipListener  {

    var loginView: ILoginView?= null
    var loginInteractor: LoginInteractor?= null

    override fun onAttach(loginView: ILoginView) {
        this.loginView = loginView
        this.loginInteractor = LoginInteractor()

        if (loginInteractor!!.isAnonymous()) {
            loginView.skipLogin()
            return
        }

        if(loginInteractor!!.isLoggedIn()) {
            loginView.skipLogin()
            return
        }

        loginView.attachEmails(loginInteractor?.getSavedEmails())
    }

    override fun skipLogin() {
        loginInteractor?.skipLogin(this)
    }

    override fun onSkip() {
        loginView?.skipLogin()
    }

    override fun login(email: String, password: String, isSusiServerSelected: Boolean, context: Context, url: String) {
        loginInteractor?.login(email, password, isSusiServerSelected, url, context, this)
    }

    override fun showProgress() {
        loginView?.showProgress()
    }

    override fun hideProgress() {
        loginView?.hideProgress()
    }

    override fun cancelLogin() {
        loginInteractor!!.cancelLogin()
    }

    override fun incorrectEmail() {
        loginView?.incorrectEmailError()
    }

    override fun emptyEmail() {
        loginView?.emptyEmailError()
    }

    override fun emptyPassword() {
        loginView?.emptyPasswordError()
    }

    override fun emptyURL() {
        loginView?.emptyURLError()
    }

    override fun invalidURL() {
        loginView?.invalidURLError()
    }

    override fun onError(title: String, message: String) {
        loginView?.onLoginError(title, message)
    }

    override fun onSuccess(message: String) {
        loginView?.onLoginSuccess(message)
    }

    override fun onDetach() {
        loginView = null
    }
}
