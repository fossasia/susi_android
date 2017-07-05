package org.fossasia.susi.ai.Signup

import android.app.ProgressDialog

/**
 * Created by mayanktripathi on 05/07/17.
 */
interface ISignUpView {

    fun alertSuccess()

    fun alertFailure()

    fun alertError(title: String, message: String)

    fun setErrorEmail(msg: String)

    fun setErrorPass(msg: String)

    fun setErrorConpass(msg: String)

    fun setErrorUrl(msg: String)

    fun enableSignUp(bool: Boolean)

    fun isPersonalServer(): Boolean?

    fun clearField()

    fun checkIfEmptyUrl(): Boolean

    fun setupPasswordWatcher()

    fun getValidURL(): String

    fun showProcess(): ProgressDialog

    fun  checkCredentials(): Boolean

    fun  isEmailValid(email: String): Boolean

    fun  checkPasswordValid(): Boolean

    fun  isURLValid(): Boolean

}