package org.fossasia.susi.ai.login.contract

/**
 * The interface for Login view
 *
 * Created by chiragw15 on 4/7/17.
 */
interface ILoginView {

    fun onLoginSuccess(message: String?)

    fun skipLogin()

    fun invalidCredentials(isEmpty: Boolean, what: String)

    fun showProgress(boolean: Boolean)

    fun onLoginError(title: String?, message: String?)

    fun attachEmails(savedEmails: MutableSet<String>?)
}
