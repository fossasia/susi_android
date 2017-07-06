package org.fossasia.susi.ai.Signup

import android.app.ProgressDialog


/**
 * Created by mayanktripathi on 05/07/17.
 */
interface ISignUpInteractor {

    interface OnLoginFinishedListener {

        fun enableSignUp(b: Boolean)
        fun checkCredentials(): Boolean?
        fun isEmailValid(email: String): Boolean
        fun alertError(string: String)
        fun alertFailure()
        fun clearField()
        fun alertSuccess()
        fun showProcess(): ProgressDialog
        fun setErrorEmail(boolean: Boolean)
        fun checkPasswordValid(): Boolean
        fun setErrorPass()
        fun isPersonalServer(): Boolean?
        fun checkIfEmptyUrl(): Boolean
        fun isURLValid(): Boolean?
        fun getValidURL(): String
        fun setErrorUrl()

    }

    fun signUp(email: String, password: String, conpass: String, listener: ISignUpInteractor.OnLoginFinishedListener)

}