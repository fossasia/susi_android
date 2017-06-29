package org.fossasia.susi.ai.data.contract

import org.fossasia.susi.ai.rest.responses.susi.ChangeSettingResponse
import retrofit2.Response

/**
 * Created by meeera on 14/7/17.
 */
interface ISettingModel {

    interface onSettingFinishListener {
        fun onSuccess(response: Response<ChangeSettingResponse>)
        fun onFailure(throwable: Throwable)
    }

    fun sendSetting(key: String, value: String, listener: onSettingFinishListener)
}