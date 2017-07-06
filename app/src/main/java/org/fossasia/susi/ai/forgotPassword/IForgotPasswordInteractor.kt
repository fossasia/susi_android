package org.fossasia.susi.ai.forgotPassword

import android.content.Context

/**
 * Created by meeera on 7/7/17.
 */
interface IForgotPasswordInteractor {

    interface onFinishListener {
        fun emptyEmail()
        fun wrongEmail()
        fun invalidUrl()
        fun emptyUrl()
        fun showProgress()
        fun hideProgress()
        fun success(title: String, message: String)
        fun failure(title: String, message: String, button: String, color: Int)
    }

    fun signup(email: String, url: String, isPersonalServerChecked: Boolean, context: Context, listener: IForgotPasswordInteractor.onFinishListener)
    fun cancelSignup()
}