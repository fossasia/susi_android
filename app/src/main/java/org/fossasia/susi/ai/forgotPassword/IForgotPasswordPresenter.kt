package org.fossasia.susi.ai.forgotPassword

import android.content.Context

/**
 * Created by meeera on 7/7/17.
 */
interface IForgotPasswordPresenter {

    fun onAttach(forgotPasswordView: IForgotPasswordView)
    fun signup(email: String, url: String, isPersonalServerChecked: Boolean, context: Context)
    fun cancelSignup()
    fun onDetach()
}