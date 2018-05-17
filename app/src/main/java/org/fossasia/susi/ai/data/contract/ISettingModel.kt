package org.fossasia.susi.ai.data.contract

import org.fossasia.susi.ai.rest.responses.susi.ChangeSettingResponse
import org.fossasia.susi.ai.rest.responses.susi.ResetPasswordResponse
import retrofit2.Response

/**
 * Created by meeera on 14/7/17.
 */
interface ISettingModel {

    interface OnSettingFinishListener {
        fun onSuccess(response: Response<ChangeSettingResponse>)
        fun onResetPasswordSuccess(response: Response<ResetPasswordResponse>?)
        fun onFailure(throwable: Throwable)
    }

    fun sendSetting(key: String, value: String, count: Int, listener: OnSettingFinishListener)

    fun resetPassword(password: String, newPassword: String, listener: OnSettingFinishListener)
}