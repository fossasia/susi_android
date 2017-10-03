package org.fossasia.susi.ai.data.contract

import retrofit2.Response

/**
 * The interface for Login Model
 *
 * Created by chiragw15 on 4/7/17.
 */
interface ILoginModel {

    interface OnLoginFinishedListener {
        fun onError(throwable: Throwable)
        fun onSuccess(response: Response<LoginResponse>)
        fun onSuccessSetting(response: Response<UserSetting>)
        fun onErrorSetting()
    }

    fun login(email: String, password: String, listener: OnLoginFinishedListener)

    fun cancelLogin()

    fun getUserSetting(listener: OnLoginFinishedListener)
}
