package org.fossasia.susi.ai.data

import org.fossasia.susi.ai.data.contract.ISettingModel
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.PrefManager
import org.fossasia.susi.ai.rest.ClientBuilder
import org.fossasia.susi.ai.rest.responses.susi.ChangeSettingResponse
import org.fossasia.susi.ai.rest.responses.susi.ResetPasswordResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by meeera on 14/7/17.
 */
class SettingModel: ISettingModel {

    lateinit var settingResponseCall: Call<ChangeSettingResponse>
    lateinit var resetPasswordResponseCall: Call<ResetPasswordResponse>
    override fun sendSetting(key: String, value: String, count: Int, listener: ISettingModel.onSettingFinishListener) {
        settingResponseCall = ClientBuilder().susiApi
                .changeSettingResponse(key, value, count)
        settingResponseCall.enqueue(object : Callback<ChangeSettingResponse> {
            override fun onFailure(call: Call<ChangeSettingResponse>?, t: Throwable) {
                listener.onFailure(t)
            }

            override fun onResponse(call: Call<ChangeSettingResponse>?, response: Response<ChangeSettingResponse>) {
                listener.onSuccess(response)
            }

        })
    }

    override fun resetPassword(password: String, newPassword: String, listener: ISettingModel.onSettingFinishListener) {
        val email = PrefManager.getString(Constant.SAVE_EMAIL, null)
        resetPasswordResponseCall = ClientBuilder().susiApi
                .resetPasswordResponse(email,password,newPassword)
        resetPasswordResponseCall.enqueue(object : Callback<ResetPasswordResponse> {
            override fun onResponse(call: Call<ResetPasswordResponse>?, response: Response<ResetPasswordResponse>?) {
                listener.onResetPasswordSuccess(response)
            }

            override fun onFailure(call: Call<ResetPasswordResponse>?, t: Throwable) {
                listener.onFailure(t)
            }

        } )
    }

}