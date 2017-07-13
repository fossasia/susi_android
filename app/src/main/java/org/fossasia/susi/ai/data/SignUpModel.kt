package org.fossasia.susi.ai.data

import org.fossasia.susi.ai.data.contract.ISignUpModel
import org.fossasia.susi.ai.rest.ClientBuilder
import org.fossasia.susi.ai.rest.responses.susi.SignUpResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Model of Login
 * The M in MVP
 * Stores all business logic
 *
 * Created by mayanktripathi on 10/07/17.
 */

class SignUpModel: ISignUpModel {

    lateinit var authResponseCall: Call<SignUpResponse>

    override fun signUp(email: String, password: String, listener: ISignUpModel.OnSignUpFinishedListener) {

        authResponseCall = ClientBuilder().susiApi
                .signUp(email, password)

        authResponseCall.enqueue(object : Callback<SignUpResponse> {
            override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                listener.onSuccess(response)
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                t.printStackTrace()
                listener.onError(t)
            }
        })
    }

    override fun cancelSignUp() {
        authResponseCall.cancel()
    }
}
