package org.fossasia.susi.ai.data.contract

import org.fossasia.susi.ai.rest.responses.susi.SignUpResponse
import retrofit2.Response

/**
 * The interface for SingUp Model
 *
 * Created by mayanktripathi on 10/07/17.
 */

interface ISignUpModel {

    interface OnSignUpFinishedListener {
        fun onError(throwable: Throwable)
        fun onSuccess(response: Response<SignUpResponse>)
    }

    fun signUp(email: String, password: String, listener: OnSignUpFinishedListener)

    fun cancelSignUp()
}
