package org.fossasia.susi.ai.login

import android.content.Context

/**
 * The interface for Login Presenter
 *
 * Created by chiragw15 on 4/7/17.
 */
interface ILoginPresenter {

    fun onAttach(loginView: ILoginView)

    fun login(email: String, password: String, isSusiServerSelected: Boolean, context: Context, url: String)

    fun skipLogin()

    fun cancelLogin()

    fun onDetach()
}
