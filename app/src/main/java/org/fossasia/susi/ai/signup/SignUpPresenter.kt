package org.fossasia.susi.ai.signup

import android.content.Context
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.data.SignUpModel
import org.fossasia.susi.ai.data.UtilModel
import org.fossasia.susi.ai.data.contract.ISignUpModel
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.CredentialHelper
import org.fossasia.susi.ai.rest.responses.susi.SignUpResponse
import org.fossasia.susi.ai.signup.contract.ISignUpPresenter
import org.fossasia.susi.ai.signup.contract.ISignUpView
import retrofit2.Response
import java.net.UnknownHostException

/**
 * Created by mayanktripathi on 05/07/17.
 */

class SignUpPresenter : ISignUpPresenter, ISignUpModel.OnSignUpFinishedListener {

    var signUpView: ISignUpView? = null
    var signUpModel: SignUpModel? = null
    var utilModel: UtilModel? = null
    var email: String? = null
    var context: Context? = null

    override fun onAttach(signUpView: ISignUpView) {
        this.signUpView = signUpView
        this.signUpModel = SignUpModel()
        this.utilModel = UtilModel()
    }

    override fun signUp(email: String, password: String, conpass: String, isSusiServerSelected: Boolean, context: Context, url: String) {

        if (email.isEmpty()) {
            signUpView?.invalidCredentials(true, Constant.EMAIL)
            return
        }
        if (password.isEmpty()) {
            signUpView?.invalidCredentials(true, Constant.PASSWORD)
            return
        }
        if (conpass.isEmpty()) {
            signUpView?.invalidCredentials(true, Constant.CONFIRM_PASSWORD)
            return
        }
        if (!CredentialHelper.isEmailValid(email)) {
            signUpView?.invalidCredentials(false, Constant.EMAIL)
            return
        }
        if (!CredentialHelper.isPasswordValid(password)) {
            signUpView?.passwordInvalid()
            return
        }
        if (password != conpass) {
            signUpView?.invalidCredentials(false, Constant.PASSWORD)
            return
        }
        if (!isSusiServerSelected) {
            if (!url.isEmpty() && CredentialHelper.isURLValid(url)) {
                if (CredentialHelper.getValidURL(url) != null) {
                    utilModel?.setServer(false)
                    utilModel?.setCustomURL(url)
                } else {
                    signUpView?.invalidCredentials(false, Constant.INPUT_URL)
                    return
                }
            } else {
                signUpView?.invalidCredentials(false, Constant.INPUT_URL)
                return
            }
        } else {
            utilModel?.setServer(true)
        }

        this.email = email
        this.context = context
        signUpView?.showProgress(true)
        signUpModel?.signUp(email.trim({ it <= ' ' }).toLowerCase(), password, this)

    }

    override fun onError(throwable: Throwable) {
        signUpView?.showProgress(false)

        if (throwable is UnknownHostException) {
            signUpView?.onSignUpError(context?.getString(R.string.unknown_host_exception), throwable.message.toString())
        } else {
            signUpView?.onSignUpError(context?.getString(R.string.error_internet_connectivity),
                    context?.getString(R.string.no_internet_connection))
        }
    }

    override fun onSuccess(response: Response<SignUpResponse>) {
        signUpView?.showProgress(false)
        if (response.isSuccessful && response.body() != null) {
            signUpView?.alertSuccess()
            signUpView?.clearField()

        } else {
            if (response.code() == 422) {
                signUpView?.alertFailure()
            } else {
                signUpView?.onSignUpError("${response.code()} " + context?.getString(R.string.error), response.message())
            }
        }
        signUpView?.showProgress(false)
    }

    override fun checkForPassword(password: String) {
        if (!CredentialHelper.isPasswordValid(password))
            signUpView?.passwordInvalid()
    }

    override fun cancelSignUp() {
        signUpModel?.cancelSignUp()
    }

    override fun onDetach() {
        signUpView = null
    }
}