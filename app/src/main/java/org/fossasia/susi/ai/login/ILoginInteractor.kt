package org.fossasia.susi.ai.login

import android.content.Context

/**
 * The interface for Login Interactor
 *
 * Created by chiragw15 on 4/7/17.
 */
interface ILoginInteractor {

    interface OnLoginFinishedListener {
        fun onError(title: String, message: String)
        fun onSuccess(message: String)
        fun incorrectEmail()
        fun emptyEmail()
        fun emptyPassword()
        fun emptyURL()
        fun invalidURL()
        fun showProgress()
        fun hideProgress()
    }

    interface OnLoginSkipListener{
       fun onSkip()
    }

    fun login(email: String, password: String, isSusiServerSelected: Boolean, url: String, context: Context, listener: OnLoginFinishedListener)

    fun isLoggedIn(): Boolean

    fun isAnonymous(): Boolean

    fun getSavedEmails(): MutableSet<String>?

    fun skipLogin(listener: OnLoginSkipListener)

    fun cancelLogin()
}
