package org.fossasia.susi.ai.data.contract

import org.fossasia.susi.ai.rest.responses.susi.ForgotPasswordResponse
import retrofit2.Response

/**
 * The interface for ForgotPassword Model
 *
 * Created by meeera on 10/7/17.
 */
interface IForgotPasswordModel {

    interface onFinishListener {
        fun onError(throwable: Throwable)
        fun onSuccess(response: Response<ForgotPasswordResponse>)
    }

    fun requestPassword(email: String, listener: IForgotPasswordModel.onFinishListener)
    fun cancelSignup()
}