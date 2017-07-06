package org.fossasia.susi.ai.signup

import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.CredentialHelper
import org.fossasia.susi.ai.helper.PrefManager
import org.fossasia.susi.ai.rest.ClientBuilder
import org.fossasia.susi.ai.rest.responses.susi.SignUpResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.UnknownHostException

/**
 * Created by mayanktripathi on 05/07/17.
 */
class SignUpInteractor: ISignUpInteractor {

    override fun signUp(email: String, password: String, conpass: String, isSusiServerSelected: Boolean, url: String, listener: ISignUpInteractor.OnLoginFinishedListener) {
        if(email.isEmpty()){
            listener.emptyEmail()
            return
        }
        if(password.isEmpty()){
            listener.emptyPassword()
            return
        }
        if(conpass.isEmpty()){
            listener.emptyConPassword()
            return
        }
        if (!CredentialHelper.isEmailValid(email)) {
            listener.setErrorEmail()
            return
        }
        if (!CredentialHelper.isPasswordValid(password)) {
            listener.passwordInvalid()
            return
        }
        if (password != conpass) {
            listener.setErrorPass()
            return
        }
        if(!isSusiServerSelected) {
            if(!url.isEmpty() && CredentialHelper.isURLValid(url)) {
                if (CredentialHelper.getValidURL(url) != null) {
                    PrefManager.putBoolean(Constant.SUSI_SERVER, false)
                    PrefManager.putString(Constant.CUSTOM_SERVER, CredentialHelper.getValidURL(url))
                } else {
                    listener.setErrorUrl()
                    return
                }
            } else {
                listener.setErrorUrl()
                return
            }
        } else{
            PrefManager.putBoolean(Constant.SUSI_SERVER, true)
        }
        listener.enableSignUp(false)

        val signUpCall = ClientBuilder().susiApi
                .signUp(email.trim(), password)

        listener.showProgress()

        signUpCall?.enqueue(object : Callback<SignUpResponse> {
            override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    listener.alertSuccess()
                    listener.clearField()
                } else {
                    if(response.code() == 422) {
                        listener.alertFailure()
                    } else {
                        listener.alertError(response.message())
                    }

                }
                listener.enableSignUp(true)
                listener.hideProgress()
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                t.printStackTrace()
                if( t is UnknownHostException) {
                    listener.alertError(t.message.toString())
                    listener.enableSignUp(true)
                    listener.hideProgress()
                } else {
                    listener.enableSignUp(true)
                    listener.hideProgress()
                }
            }
        })
    }

    override fun isvalidPassword(password: String): Boolean {
        return CredentialHelper.isPasswordValid(password)
    }
}