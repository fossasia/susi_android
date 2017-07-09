package org.fossasia.susi.ai.forgotPassword

import android.content.Context
import android.graphics.Color
import android.util.Patterns
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.CredentialHelper
import org.fossasia.susi.ai.helper.PrefManager
import org.fossasia.susi.ai.rest.ClientBuilder
import org.fossasia.susi.ai.rest.responses.susi.ForgotPasswordResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.UnknownHostException

/**
 * Created by meeera on 7/7/17.
 */
class ForgotPasswordInteractor: IForgotPasswordInteractor {

    var authResponseCall: Call<ForgotPasswordResponse>? = null

    override fun signup(email: String, url: String, isPersonalServerChecked: Boolean, context: Context, listener: IForgotPasswordInteractor.onFinishListener) {
        if (email.isEmpty()) {
            listener.emptyEmail()
            return
        }

        if (!CredentialHelper.isEmailValid(email)) {
            listener.wrongEmail()
            return
        }

        if (isPersonalServerChecked) {
            if(url.isEmpty()) {
                listener.emptyUrl()
                return
            }
            if ( Patterns.WEB_URL.matcher(url).matches()) {
                if (CredentialHelper.getValidURL(url) != null) {
                    PrefManager.putBoolean(Constant.SUSI_SERVER, false)
                    PrefManager.putString(Constant.CUSTOM_SERVER, CredentialHelper.getValidURL(url))
                } else {
                    listener.invalidUrl()
                    return
                }
            } else {
                listener.invalidUrl()
                return
            }
        } else {
            PrefManager.putBoolean(Constant.SUSI_SERVER, true)
        }

        listener.showProgress()

        authResponseCall = ClientBuilder().susiApi
                .forgotPassword(email.trim({ it <= ' ' }))

        authResponseCall?.enqueue(object : Callback<ForgotPasswordResponse> {
            override fun onResponse(call: Call<ForgotPasswordResponse>, response: Response<ForgotPasswordResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    listener.success(context.getString(R.string.forgot_password_mail_sent), response.body().message)
                } else if (response.code() == 422) {
                    listener.failure(context.getString(R.string.email_invalid_title), context.getString(R.string.email_invalid), context.getString(R.string.retry), Color.RED)
                } else {
                    listener.failure("${response.code()} " + context.getString(R.string.error), response.message(), context.getString(R.string.ok), Color.BLUE)
                }
                listener.hideProgress()
            }

            override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {
                t.printStackTrace()
                if (t is UnknownHostException) {
                    listener.failure(context.getString(R.string.unknown_host_exception), (t.message).toString(), context.getString(R.string.retry), Color.RED)
                } else {
                    listener.failure(context.getString(R.string.error_internet_connectivity), context.getString(R.string.no_internet_connection), context.getString(R.string.retry), Color.RED)
                }
                listener.hideProgress()
            }
        })
    }

    override fun cancelSignup() {
        authResponseCall?.cancel()
    }
}