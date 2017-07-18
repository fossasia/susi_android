package org.fossasia.susi.ai.data

import org.fossasia.susi.ai.data.contract.ILoginModel
import org.fossasia.susi.ai.rest.ClientBuilder
import org.fossasia.susi.ai.rest.responses.susi.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Model of Login
 * The M in MVP
 * Stores all business logic
 *
 * Created by chiragw15 on 4/7/17.
 */

class LoginModel : ILoginModel {

    lateinit var authResponseCall: Call<LoginResponse>

    override fun login(email: String, password: String, listener: ILoginModel.OnLoginFinishedListener) {

        authResponseCall = ClientBuilder().susiApi
                .login(email, password)

        authResponseCall.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                listener.onSuccess(response)
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                t.printStackTrace()
                listener.onError(t)
            }
        })
    }

    override fun cancelLogin() {
        authResponseCall.cancel()
    }
}
