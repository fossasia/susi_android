package org.fossasia.susi.ai.login

import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.data.contract.ILoginModel
import org.fossasia.susi.ai.data.LoginModel
import org.fossasia.susi.ai.data.UtilModel
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.CredentialHelper
import org.fossasia.susi.ai.login.contract.ILoginPresenter
import org.fossasia.susi.ai.login.contract.ILoginView
import org.fossasia.susi.ai.rest.responses.susi.LoginResponse
import retrofit2.Response
import java.net.UnknownHostException

/**
 * Presenter for Login
 * The P in MVP
 *
 * Created by chiragw15 on 4/7/17.
 */
class LoginPresenter(loginActivity: LoginActivity): ILoginPresenter, ILoginModel.OnLoginFinishedListener {

    var loginModel: LoginModel = LoginModel()
    var utilModel: UtilModel = UtilModel(loginActivity)
    var loginView: ILoginView?= null
    lateinit var email: String

    override fun onAttach(loginView: ILoginView) {
        this.loginView = loginView

        if (utilModel.getAnonymity()) {
            loginView.skipLogin()
            return
        }

        if(utilModel.isLoggedIn()) {
            loginView.skipLogin()
            return
        }

        loginView.attachEmails(utilModel.getSavedEmails())
    }

    override fun skipLogin() {
        utilModel.clearToken()
        utilModel.saveAnonymity(true)
        loginView?.skipLogin()
    }

    override fun login(email: String, password: String, isSusiServerSelected: Boolean, url: String) {
        if (email.isEmpty()) {
            loginView?.invalidCredentials(true, Constant.EMAIL)
            return
        }

        if(password.isEmpty()) {
            loginView?.invalidCredentials(true, Constant.PASSWORD)
            return
        }

        if (!CredentialHelper.isEmailValid(email)) {
            loginView?.invalidCredentials(false, Constant.EMAIL)
            return
        }

        if (!isSusiServerSelected) {
            if (url.isEmpty()){
                loginView?.invalidCredentials(true, Constant.INPUT_URL)
                return
            }

            if( CredentialHelper.isURLValid(url)) {
                if (CredentialHelper.getValidURL(url) != null) {
                    utilModel.setServer(false)
                    utilModel.setCustomURL(url)
                } else {
                    loginView?.invalidCredentials(false, Constant.INPUT_URL)
                    return
                }
            } else {
                loginView?.invalidCredentials(false, Constant.INPUT_URL)
                return
            }
        } else {
            utilModel.setServer(true)
        }

        this.email = email
        loginView?.showProgress(true)
        loginModel.login(email.trim({ it <= ' ' }).toLowerCase(), password, this)
    }

    override fun cancelLogin() {
        loginModel.cancelLogin()
    }

    override fun onError(throwable: Throwable) {
        loginView?.showProgress(false)

        if (throwable is UnknownHostException) {
            loginView?.onLoginError(utilModel.getString(R.string.unknown_host_exception), throwable.message.toString())
        } else {
            loginView?.onLoginError(utilModel.getString(R.string.error_internet_connectivity),
                    utilModel.getString(R.string.no_internet_connection))
        }
    }

    override fun onSuccess(response: Response<LoginResponse>) {

        loginView?.showProgress(false)

        if (response.isSuccessful && response.body() != null) {

            utilModel.saveToken(response)
            utilModel.deleteAllMessages()
            utilModel.saveEmail(email)
            utilModel.saveAnonymity(false)

            loginView?.onLoginSuccess(response.body().message)
        } else if (response.code() == 422) {
            loginView?.onLoginError(utilModel.getString(R.string.password_invalid_title),
                    utilModel.getString(R.string.password_invalid))
        } else {
            loginView?.onLoginError("${response.code()} " + utilModel.getString(R.string.error), response.message())
        }
    }

    override fun onDetach() {
        loginView = null
    }
}
