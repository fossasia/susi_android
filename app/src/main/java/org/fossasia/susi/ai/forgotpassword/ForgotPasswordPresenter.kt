package org.fossasia.susi.ai.forgotpassword

import android.graphics.Color
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.data.ForgotPasswordModel
import org.fossasia.susi.ai.data.UtilModel
import org.fossasia.susi.ai.data.contract.IForgotPasswordModel
import org.fossasia.susi.ai.forgotpassword.contract.IForgotPasswordPresenter
import org.fossasia.susi.ai.forgotpassword.contract.IForgotPasswordView
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.CredentialHelper
import org.fossasia.susi.ai.rest.responses.susi.ForgotPasswordResponse
import retrofit2.Response
import java.net.UnknownHostException

/**
 * Presenter for ForgotPassword
 * The P in MVP
 *
 * Created by meeera on 6/7/17.
 */
class ForgotPasswordPresenter(forgotPasswordActivity: ForgotPasswordActivity) : IForgotPasswordPresenter, IForgotPasswordModel.onFinishListener {

    lateinit var email: String
    var forgotPasswordView: IForgotPasswordView?= null
    var forgotPasswordModel: ForgotPasswordModel = ForgotPasswordModel()
    var utilModel: UtilModel = UtilModel(forgotPasswordActivity)

    override fun onAttach(forgotPasswordView: IForgotPasswordView) {
        this.forgotPasswordView = forgotPasswordView
    }

    override fun cancelSignup() {
        forgotPasswordModel.cancelSignup()
    }

    override fun requestPassword(email: String, url: String, isPersonalServerChecked: Boolean) {
        if (email.isEmpty()) {
            forgotPasswordView?.invalidCredentials(true, Constant.EMAIL)
            return
        }

        if (!CredentialHelper.isEmailValid(email)) {
            forgotPasswordView?.invalidCredentials(false, Constant.EMAIL)
            return
        }

        if (isPersonalServerChecked) {
            if(url.isEmpty()) {
                forgotPasswordView?.invalidCredentials(true, Constant.INPUT_URL)
                return
            }
            if ( CredentialHelper.isURLValid(url)) {
                if (CredentialHelper.getValidURL(url) != null) {
                    utilModel.setServer(false)
                    utilModel.setCustomURL(CredentialHelper.getValidURL(url) as String)
                } else {
                    forgotPasswordView?.invalidCredentials(false, Constant.INPUT_URL)
                    return
                }
            } else {
                forgotPasswordView?.invalidCredentials(false, Constant.INPUT_URL)
                return
            }
        } else {
            utilModel.setServer(true)
        }
        this.email = email
        forgotPasswordView?.showProgress(true)
        forgotPasswordModel.requestPassword(email.trim({ it <= ' ' }), this)
    }

    override fun onError(throwable: Throwable) {
        forgotPasswordView?.showProgress(false)
        if (throwable is UnknownHostException) {
            forgotPasswordView?.failure(utilModel.getString(R.string.unknown_host_exception), (throwable.message).toString(), utilModel.getString(R.string.retry), Color.RED)
        } else {
            forgotPasswordView?.failure(utilModel.getString(R.string.error_internet_connectivity), utilModel.getString(R.string.no_internet_connection), utilModel.getString(R.string.retry), Color.RED)
        }
    }

    override fun onSuccess(response: Response<ForgotPasswordResponse>) {
        forgotPasswordView?.showProgress(false)
        if (response.isSuccessful && response.body() != null) {
            forgotPasswordView?.success(utilModel.getString(R.string.forgot_password_mail_sent), response.body().message)
        } else if (response.code() == 422) {
            forgotPasswordView?.failure(utilModel.getString(R.string.email_invalid_title), utilModel.getString(R.string.email_invalid), utilModel.getString(R.string.retry), Color.RED)
        } else {
            forgotPasswordView?.failure("${response.code()} " + utilModel.getString(R.string.error), response.message(), utilModel.getString(R.string.ok), Color.BLUE)
        }
    }

    override fun onDetach() {
        forgotPasswordView = null
    }
}