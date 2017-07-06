package org.fossasia.susi.ai.signup

import android.app.ProgressDialog

/**
 * Created by mayanktripathi on 05/07/17.
 */
interface ISignUpInteractor {

    interface OnLoginFinishedListener {

        fun enableSignUp(b: Boolean)
        fun alertError(string: String)
        fun alertFailure()
        fun clearField()
        fun alertSuccess()
        fun showProcess(): ProgressDialog
        fun setErrorEmail()
        fun setErrorPass()
        fun isPersonalServer(): Boolean
        fun setErrorUrl()
        fun emptyEmail()
        fun emptyPassword()
        fun emptyConPassword()
        fun passwordInvalid()

    }

    fun signUp(email: String, password: String, conpass: String, url: String, listener: ISignUpInteractor.OnLoginFinishedListener)

}