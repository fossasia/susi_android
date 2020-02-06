package org.fossasia.susi.ai.login.contract

import android.content.Context

/**
 * The interface for Login Presenter
 *
 * Created by chiragw15 on 4/7/17.
 */
interface ILoginPresenter {

    fun onAttach(loginView: ILoginView)

    fun login(email: String, password: String, isSusiServerSelected: Boolean, url: String)

    fun skipLogin()

    fun cancelLogin()

    fun requestPassword(email: String, url: String, isPersonalServerChecked: Boolean)

    fun cancelSignup()

    fun saveCredential(email: String, password: String)

    fun clientRequest(context: Context)
}
