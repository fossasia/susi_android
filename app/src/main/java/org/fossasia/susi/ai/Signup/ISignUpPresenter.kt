package org.fossasia.susi.ai.Signup

import android.content.Context


/**
 * Created by mayanktripathi on 05/07/17.
 */
interface ISignUpPresenter {

    fun onAttach(sinUpView: ISignUpView, context: Context)

    fun login(email: String, password: String, conpass: String)

    fun onDetach()

}