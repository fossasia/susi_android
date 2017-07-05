package org.fossasia.susi.ai.Signup

import android.content.Context
import android.content.DialogInterface
import android.widget.Toast
import com.google.android.youtube.player.internal.t
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.PrefManager
import org.fossasia.susi.ai.rest.ClientBuilder
import org.fossasia.susi.ai.rest.responses.susi.SignUpResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.UnknownHostException

/**
 * Created by mayanktripathi on 05/07/17.
 */

class SignUpPresenter: ISignUpPresenter {

    var signUpView: ISignUpView? = null
    var signUpInteractor: SignUpInteractor? = null
    var context: Context? = null

    override fun onAttach(signUpView: ISignUpView, context: Context) {
        this.signUpView = signUpView
        this.signUpInteractor = SignUpInteractor()
        this.context = context
    }

    override fun login(email: String, password: String, conpass: String) {
        if (signUpView?.checkCredentials()!!) {
            return
        }
        if (!signUpView?.isEmailValid(email)!!) {
            signUpView?.setErrorEmail(context?.getString(R.string.invalid_email)!!)
            return
        }
        signUpView?.setErrorEmail(null!!)
        if (!signUpView?.checkPasswordValid()!!) {
            return
        }
        if (password != conpass) {
            signUpView?.setErrorPass(context?.getString(R.string.error_password_matching)!!)
            return
        }
        if(signUpView?.isPersonalServer()!!) {
            if(!signUpView?.checkIfEmptyUrl()!! && signUpView?.isURLValid()!!) {
                if (signUpView?.getValidURL() != null) {
                    PrefManager.putBoolean(Constant.SUSI_SERVER, false)
                    PrefManager.putString(Constant.CUSTOM_SERVER, signUpView?.getValidURL())
                } else {
                    signUpView?.setErrorUrl(context?.getString(R.string.invalid_url)!!)
                    return
                }
            } else {
                return
            }
        } else{
            PrefManager.putBoolean(Constant.SUSI_SERVER, true);
        }
        signUpView?.setErrorConpass(null);
        signUpView?.enableSignUp(false);
        var progressDialog = signUpView?.showProcess();

        var signUpCall = ClientBuilder().getSusiApi()
                .signUp(email.trim(), password)
        progressDialog?.setOnCancelListener(DialogInterface.OnCancelListener {  })

        signUpCall.enqueue(Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    signUpView?.alertSuccess()
                    signUpView.clearFiled()
                } else {
                    if(response.code() == 422) {
                        signUpView?.alertFailure()
                    } else {
                        signUpView?.alertError(response.code() + context?.getString(R.string.error), response.message())
                    }

                }
                signUpView?.enableSignUp(true)
                progressDialog?.dismiss()
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                t.printStackTrace()
                if( t instanceof UnknownHostException) {
                    signUpView?.alertError(context.getString(R.string.unknown_host_exception), t.getMessage())
                    signUpView?.enableSignUp(true)
                    progressDialog.dismiss()
                } else {
                    Toast.makeText(context, context.getString(R.string.internet_connection_prompt), Toast.LENGTH_SHORT).show()
                    signUpView?.enableSignUp(true)
                    progressDialog?.dismiss()
                }
            }
        });
    }

    override fun onDetach() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}