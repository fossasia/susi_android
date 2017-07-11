package org.fossasia.susi.ai.signup.contract

import android.content.Context

/**
 * Created by mayanktripathi on 05/07/17.
 */
interface ISignUpPresenter {

    fun onAttach(signUpView: ISignUpView)

    fun signUp(email: String, password: String, conpass: String, isSusiServerSelected: Boolean, context: Context, url: String)

    fun onDetach()

    fun cancelSignUp()

    fun checkForPassword(password: String)

}
