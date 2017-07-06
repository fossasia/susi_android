package org.fossasia.susi.ai.signup

/**
 * Created by mayanktripathi on 05/07/17.
 */
interface ISignUpPresenter {

    fun onAttach(signUpView: ISignUpView)

    fun signUp(email: String, password: String, conpass: String, url: String)

    fun onDetach()

}
