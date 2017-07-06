package org.fossasia.susi.ai.Signup

import android.util.Log
import org.fossasia.susi.ai.helper.Constant
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

    override fun signUp(email: String, password: String, conpass: String, listener: ISignUpInteractor.OnLoginFinishedListener) {
        if (listener.checkCredentials()!!) {
            return
        }
        if (!listener.isEmailValid(email)) {
            listener.setErrorEmail(true)
            return
        }
        listener.setErrorEmail(false)
        if (!listener.checkPasswordValid()) {
            return
        }
        if (password != conpass) {
            listener.setErrorPass()
            return
        }
        if(listener.isPersonalServer()!!) {
            if(!listener.checkIfEmptyUrl() && listener.isURLValid()!!) {
                if (listener?.getValidURL() != null) {
                    PrefManager.putBoolean(Constant.SUSI_SERVER, false)
                    PrefManager.putString(Constant.CUSTOM_SERVER, listener.getValidURL())
                } else {
                    listener.setErrorUrl()
                    return
                }
            } else {
                return
            }
        } else{
            PrefManager.putBoolean(Constant.SUSI_SERVER, true)
        }
        listener.enableSignUp(false)
        val progressDialog = listener.showProcess()

        val signUpCall = ClientBuilder().susiApi
                .signUp(email.trim(), password)

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
                progressDialog.dismiss()
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                t.printStackTrace()
                if( t is UnknownHostException) {
                    listener.alertError(t.message.toString())
                    listener.enableSignUp(true)
                    progressDialog.dismiss()
                } else {
                    listener.enableSignUp(true)
                    progressDialog.dismiss()
                }
            }
        })
    }
}