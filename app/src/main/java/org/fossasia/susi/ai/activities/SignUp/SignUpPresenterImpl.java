package org.fossasia.susi.ai.activities.SignUp;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.helper.Constant;
import org.fossasia.susi.ai.helper.PrefManager;
import org.fossasia.susi.ai.rest.ClientBuilder;
import org.fossasia.susi.ai.rest.responses.susi.SignUpResponse;

import java.net.UnknownHostException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.fossasia.susi.ai.activities.MainActivity.TAG;

/**
 * Created by mayanktripathi on 05/07/17.
 */

public class SignUpPresenterImpl implements SignUpInteractor{

    SignUpView signUpView;
    Context context;

    public SignUpPresenterImpl(SignUpView signUpView, Context context){

        this.signUpView = signUpView;
        this.context = context;

    }

    @Override
    public void signUp(String username, String password, final String conPass) {
            if (signUpView.checkCredentials()) {
                return;
            }
            if (!signUpView.isEmailValid(username)) {
                Log.d(TAG, "signUp: not valid" + username);
                signUpView.setErrorEmail(context.getString(R.string.invalid_email));
                return;
            }
            signUpView.setErrorEmail(null);
            if (!signUpView.checkPasswordValid()) {
                return;
            }
            if (!password.equals(conPass)) {
                signUpView.setErrorPass(context.getString(R.string.error_password_matching));
                return;
            }
            if(signUpView.isPersonalServer()) {
                if(!signUpView.checkIfEmptyUrl() && signUpView.isURLValid()) {
                    if (signUpView.getValidURL() != null) {
                        PrefManager.putBoolean(Constant.SUSI_SERVER, false);
                        PrefManager.putString(Constant.CUSTOM_SERVER, signUpView.getValidURL());
                    } else {
                        signUpView.setErrorUrl(context.getString(R.string.invalid_url));
                        return;
                    }
                } else {
                    return;
                }
            } else{
                PrefManager.putBoolean(Constant.SUSI_SERVER, true);
            }
            signUpView.setErrorConpass(null);
            signUpView.enableSignUp(false);
            final ProgressDialog progressDialog = signUpView.showProcess();

            final Call<SignUpResponse> signUpCall = new ClientBuilder().getSusiApi()
                    .signUp(username.trim(), password);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    signUpCall.cancel();
                    signUpView.enableSignUp(true);
                }
            });

            signUpCall.enqueue(new Callback<SignUpResponse>() {
                @Override
                public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        signUpView.alertSuccess();
                        signUpView.clearFiled();
                        Log.d(TAG, "onResponse: " + "success");
                    } else {
                        if(response.code() == 422) {
                            signUpView.alertFailure();
                        } else {
                            signUpView.alertError(response.code() + context.getString(R.string.error), response.message());
                        }

                    }
                    signUpView.enableSignUp(true);
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<SignUpResponse> call, Throwable t) {
                    t.printStackTrace();
                    if( t instanceof UnknownHostException) {
                        signUpView.alertError(context.getString(R.string.unknown_host_exception), t.getMessage());
                        signUpView.enableSignUp(true);
                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(context, context.getString(R.string.internet_connection_prompt), Toast.LENGTH_SHORT).show();
                        signUpView.enableSignUp(true);
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }

