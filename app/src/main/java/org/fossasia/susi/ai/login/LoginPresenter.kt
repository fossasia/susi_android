package org.fossasia.susi.ai.login

import android.graphics.Color
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.data.ForgotPasswordModel
import org.fossasia.susi.ai.data.contract.ILoginModel
import org.fossasia.susi.ai.data.contract.IForgotPasswordModel
import org.fossasia.susi.ai.data.LoginModel
import org.fossasia.susi.ai.data.UtilModel
import org.fossasia.susi.ai.data.db.DatabaseRepository
import org.fossasia.susi.ai.data.db.contract.IDatabaseRepository
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.CredentialHelper
import org.fossasia.susi.ai.helper.NetworkUtils
import org.fossasia.susi.ai.login.contract.ILoginPresenter
import org.fossasia.susi.ai.login.contract.ILoginView
import org.fossasia.susi.ai.rest.responses.susi.ForgotPasswordResponse
import org.fossasia.susi.ai.rest.responses.susi.LoginResponse
import org.fossasia.susi.ai.rest.responses.susi.Settings
import org.fossasia.susi.ai.rest.responses.susi.UserSetting
import retrofit2.Response
import timber.log.Timber
import java.net.UnknownHostException

/**
 * Presenter for Login
 * The P in MVP
 *
 * Created by chiragw15 on 4/7/17.
 */
class LoginPresenter(loginActivity: LoginActivity) : ILoginPresenter, ILoginModel.OnLoginFinishedListener, IForgotPasswordModel.OnFinishListener {

    private var loginModel: LoginModel = LoginModel()
    private var utilModel: UtilModel = UtilModel(loginActivity)
    private var databaseRepository: IDatabaseRepository = DatabaseRepository()
    private var loginView: ILoginView? = null
    var forgotPasswordModel: ForgotPasswordModel = ForgotPasswordModel()
    lateinit var email: String
    lateinit var message: String

    override fun onAttach(loginView: ILoginView) {
        this.loginView = loginView

        if (utilModel.getAnonymity()) {
            loginView.skipLogin()
            return
        }

        if (utilModel.isLoggedIn()) {
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

        if (password.isEmpty()) {
            loginView?.invalidCredentials(true, Constant.PASSWORD)
            return
        }

        if (!CredentialHelper.isEmailValid(email)) {
            loginView?.invalidCredentials(false, Constant.EMAIL)
            return
        }

        if (!isSusiServerSelected) {
            if (url.isEmpty()) {
                loginView?.invalidCredentials(true, Constant.INPUT_URL)
                return
            }

            if (CredentialHelper.isURLValid(url)) {
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
            if (NetworkUtils.isNetworkConnected()) {
                Timber.e(throwable.toString())
                loginView?.onLoginError(utilModel.getString(R.string.unknown_host_exception), throwable.message.toString())
            } else {
                loginView?.onLoginError(utilModel.getString(R.string.error_internet_connectivity),
                        utilModel.getString(R.string.no_internet_connection))
            }
        } else {
            loginView?.onLoginError(utilModel.getString(R.string.error_internet_connectivity),
                    utilModel.getString(R.string.no_internet_connection))
        }
    }

    override fun onLoginModelSuccess(response: Response<LoginResponse>) {

        if (response.isSuccessful && response.body() != null) {

            utilModel.saveToken(response)
            databaseRepository.deleteAllMessages()
            utilModel.saveEmail(email)
            utilModel.saveAnonymity(false)
            loginModel.getUserSetting(this)

            message = response.body().message.toString()
        } else if (response.code() == 422) {
            loginView?.showProgress(false)
            loginView?.onLoginError(utilModel.getString(R.string.password_invalid_title),
                    utilModel.getString(R.string.password_invalid))
        } else {
            loginView?.showProgress(false)
            loginView?.onLoginError("${response.code()} " + utilModel.getString(R.string.error),
                    "${response.message()} \n" + utilModel.getString(R.string.error_custom_server))
        }
    }

    override fun onSuccessSetting(response: Response<UserSetting>) {

        loginView?.showProgress(false)

        if (response.isSuccessful && response.body() != null) {
            val settings: Settings? = response.body().settings

            if (settings != null) {
                utilModel.putBooleanPref(Constant.ENTER_SEND, settings.enterSend)
                utilModel.putBooleanPref(Constant.SPEECH_ALWAYS, settings.speechAlways)
                utilModel.putBooleanPref(Constant.SPEECH_OUTPUT, settings.speechOutput)
                utilModel.setLanguage(settings.language)
            }

            loginView?.onLoginSuccess(message)
        }
    }

    override fun onErrorSetting() {
        loginView?.showProgress(false)
        loginView?.onLoginSuccess(message)
    }


    override fun onDetach() {
        loginView = null
    }

    override fun requestPassword(email: String, url: String, isPersonalServerChecked: Boolean) {
        if (email.isEmpty()) {
            loginView?.invalidCredentials(true, Constant.EMAIL)
            return
        }

        if (!CredentialHelper.isEmailValid(email)) {
            loginView?.invalidCredentials(false, Constant.EMAIL)
            return
        }

        if (isPersonalServerChecked) {
            if (url.isEmpty()) {
                loginView?.invalidCredentials(true, Constant.INPUT_URL)
                return
            }
            if (CredentialHelper.isURLValid(url)) {
                if (CredentialHelper.getValidURL(url) != null) {
                    utilModel.setServer(false)
                    utilModel.setCustomURL(CredentialHelper.getValidURL(url) as String)
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
        loginView?.showForgotPasswordProgress(true)
        forgotPasswordModel.requestPassword(email.trim({ it <= ' ' }), this)
    }

    override fun onForgotPasswordModelSuccess(response: Response<ForgotPasswordResponse>) {
        loginView?.showForgotPasswordProgress(false)
        if (response.isSuccessful && response.body() != null) {
            loginView?.resetPasswordSuccess()
        } else if (response.code() == 422) {
            loginView?.resetPasswordFailure(utilModel.getString(R.string.email_invalid_title), utilModel.getString(R.string.email_invalid), utilModel.getString(R.string.retry), Color.RED)
        } else {
            loginView?.resetPasswordFailure("${response.code()} " + utilModel.getString(R.string.error), response.message(), utilModel.getString(R.string.ok), Color.BLUE)
        }
    }

    override fun cancelSignup() {
        forgotPasswordModel.cancelSignup()
    }

}
