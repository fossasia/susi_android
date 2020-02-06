package org.fossasia.susi.ai.signup.contract

/**
 * The interface for SignUp Presenter
 *
 * Created by mayanktripathi on 05/07/17.
 */

interface ISignUpPresenter {

    fun signUp(email: String, password: String, conpass: String, isSusiServerSelected: Boolean, url: String, isTermsAndConditionSelected: Boolean)

    fun cancelSignUp()

    fun loginLogout()

    fun checkForPassword(password: String)

    fun requestPassword(email: String, url: String, isPersonalServerChecked: Boolean)

    fun cancelSignup()
}
