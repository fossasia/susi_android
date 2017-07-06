package org.fossasia.susi.ai.login

/**
 * The interface for Login view
 *
 * Created by chiragw15 on 4/7/17.
 */
interface ILoginView {

    fun onLoginSuccess(message: String)

    fun skipLogin()

    fun incorrectEmailError()

    fun emptyEmailError()

    fun emptyPasswordError()

    fun emptyURLError()

    fun invalidURLError()

    fun showProgress()

    fun hideProgress()

    fun onLoginError(title: String, message: String)

    fun attachEmails(savedEmails: MutableSet<String>?)
}
