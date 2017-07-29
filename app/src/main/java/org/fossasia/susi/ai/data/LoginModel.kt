package org.fossasia.susi.ai.data

import org.fossasia.susi.ai.data.contract.ILoginModel
import org.fossasia.susi.ai.helper.PrefManager
import org.fossasia.susi.ai.rest.ClientBuilder
import org.fossasia.susi.ai.rest.responses.susi.LoginResponse
import org.fossasia.susi.ai.rest.responses.susi.UserSetting
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
    lateinit var userSettingResponseCall: Call<UserSetting>

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

    override fun getUserSetting(listener: ILoginModel.OnLoginFinishedListener) {
        userSettingResponseCall = ClientBuilder().susiApi
                .getUserSetting()

        userSettingResponseCall.enqueue(object : Callback<UserSetting> {
            override fun onFailure(call: Call<UserSetting>?, t: Throwable?) {
                listener.onErrorSetting()
            }

            override fun onResponse(call: Call<UserSetting>?, response: Response<UserSetting>) {
                listener.onSuccessSetting(response)
            }

        })
    }

}
