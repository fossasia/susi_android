package org.fossasia.susi.ai.login.contract

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

    fun onDetach()
}
