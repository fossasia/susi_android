package org.fossasia.susi.ai.signup

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
        fun setErrorEmail()
        fun setErrorPass()
        fun setErrorUrl()
        fun emptyEmail()
        fun emptyPassword()
        fun emptyConPassword()
        fun passwordInvalid()
        fun showProgress()
        fun hideProgress()
        fun checkForPassword(password: String)

    }

    fun isvalidPassword(password: String): Boolean

    fun signUp(email: String, password: String, conpass: String, isSusiServerSelected: Boolean, url: String, listener: ISignUpInteractor.OnLoginFinishedListener)

}