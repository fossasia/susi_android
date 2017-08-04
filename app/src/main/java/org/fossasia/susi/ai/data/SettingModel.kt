package org.fossasia.susi.ai.data

import org.fossasia.susi.ai.data.contract.ISettingModel
import org.fossasia.susi.ai.helper.PrefManager
import org.fossasia.susi.ai.rest.ClientBuilder
import org.fossasia.susi.ai.rest.responses.susi.ChangeSettingResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by meeera on 14/7/17.
 */
class SettingModel: ISettingModel {

    lateinit var settingResponseCall: Call<ChangeSettingResponse>
    override fun sendSetting(key: String, value: String, listener: ISettingModel.onSettingFinishListener) {
        settingResponseCall = ClientBuilder().susiApi
                .changeSettingResponse(key, value)
        settingResponseCall.enqueue(object : Callback<ChangeSettingResponse> {
            override fun onFailure(call: Call<ChangeSettingResponse>?, t: Throwable) {
                listener.onFailure(t)
            }

            override fun onResponse(call: Call<ChangeSettingResponse>?, response: Response<ChangeSettingResponse>) {
                listener.onSuccess(response)
            }

        })
    }
}