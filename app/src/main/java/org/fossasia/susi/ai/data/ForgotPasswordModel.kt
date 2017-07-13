package org.fossasia.susi.ai.data

import org.fossasia.susi.ai.data.contract.IForgotPasswordModel
import org.fossasia.susi.ai.rest.ClientBuilder
import org.fossasia.susi.ai.rest.responses.susi.ForgotPasswordResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Model of ForgotPassword
 * The M in MVP
 * Stores all business logic
 *
 * Created by meeera on 10/7/17.
 */
class ForgotPasswordModel : IForgotPasswordModel {

    lateinit var authResponseCall: Call<ForgotPasswordResponse>
    override fun requestPassword(email: String, listener: IForgotPasswordModel.onFinishListener) {
        authResponseCall = ClientBuilder().susiApi.forgotPassword(email)

        authResponseCall.enqueue(object: Callback<ForgotPasswordResponse>{
            override fun onFailure(call: Call<ForgotPasswordResponse>?, t: Throwable) {
                listener.onError(t)
            }

            override fun onResponse(call: Call<ForgotPasswordResponse>?, response: Response<ForgotPasswordResponse>) {
                listener.onSuccess(response)
            }
        })
    }

    override fun cancelSignup() {
        authResponseCall.cancel()
    }
}