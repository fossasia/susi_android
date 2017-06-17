package org.fossasia.susi.ai.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.helper.Constant;
import org.fossasia.susi.ai.helper.CredentialHelper;
import org.fossasia.susi.ai.helper.PrefManager;
import org.fossasia.susi.ai.rest.ClientBuilder;
import org.fossasia.susi.ai.rest.responses.susi.SignUpResponse;

import java.net.UnknownHostException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.email)
    TextInputLayout email;
    @BindView(R.id.password)
    TextInputLayout password;
    @BindView(R.id.confirm_password)
    TextInputLayout confirmPassword;
    @BindView(R.id.sign_up)
    Button signUp;
    @BindView(R.id.susi_default)
    protected RadioButton susiServer;
    @BindView(R.id.personal_server)
    protected RadioButton personalServer;
    @BindView(R.id.input_url)
    protected TextInputLayout url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        if(savedInstanceState!=null){
            email.getEditText().setText(savedInstanceState.getCharSequenceArray(Constant.SAVED_STATES)[0].toString());
            password.getEditText().setText(savedInstanceState.getCharSequenceArray(Constant.SAVED_STATES)[1].toString());
            confirmPassword.getEditText().setText(savedInstanceState.getCharSequenceArray(Constant.SAVED_STATES)[2].toString());
            if(savedInstanceState.getBoolean(Constant.SERVER)) {
                url.setVisibility(View.VISIBLE);
            } else {
                url.setVisibility(View.GONE);
            }
        }

        setupPasswordWatcher();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if(personalServer.isChecked()) {
            url.setVisibility(View.VISIBLE);
        } else {
            url.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupPasswordWatcher() {
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    CredentialHelper.checkPasswordValid(password, SignUpActivity.this);
                }
            }
        });
    }

    @OnClick(R.id.personal_server)
    public void showURL() {
        url.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.susi_default)
    public void hideURL() {
        url.setVisibility(View.GONE);
    }

    @OnClick(R.id.sign_up)
    public void signUp() {
        if (CredentialHelper.checkIfEmpty(email, this) |
                CredentialHelper.checkIfEmpty(password, this) |
                CredentialHelper.checkIfEmpty(confirmPassword, this)) {
            return;
        }
        if (!CredentialHelper.isEmailValid(email.getEditText().getText().toString())) {
            email.setError(getApplicationContext().getString(R.string.invalid_email));
            return;
        }
        email.setError(null);
        if (!CredentialHelper.checkPasswordValid(password, this)) {
            return;
        }
        if (!password.getEditText().getText().toString()
                .equals(confirmPassword.getEditText().getText().toString())) {
            confirmPassword.setError(getApplicationContext().getString(R.string.error_password_matching));
            return;
        }
        if(personalServer.isChecked()) {
            if(!CredentialHelper.checkIfEmpty(url,this) && CredentialHelper.isURLValid(url,this)) {
                if (CredentialHelper.getValidURL(url,this) != null) {
                    PrefManager.putBoolean(Constant.SUSI_SERVER, false);
                    PrefManager.putString(Constant.CUSTOM_SERVER, CredentialHelper.getValidURL(url, this));
                } else {
                    url.setError(getApplicationContext().getString(R.string.invalid_url));
                    return;
                }
            } else {
                return;
            }
        } else{
            PrefManager.putBoolean(Constant.SUSI_SERVER, true);
        }

        confirmPassword.setError(null);
        signUp.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getApplicationContext().getString(R.string.signing_up));
        progressDialog.show();

        final Call<SignUpResponse> signUpCall = new ClientBuilder().getSusiApi()
                .signUp(email.getEditText().getText().toString().trim(),
                        password.getEditText().getText().toString());
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                signUpCall.cancel();
                signUp.setEnabled(true);
            }
        });

        signUpCall.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignUpActivity.this);
                    alertDialog.setTitle(R.string.signup);
                    alertDialog.setCancelable(false);
                    alertDialog.setMessage(R.string.signup_msg);
                    alertDialog.setPositiveButton(getApplicationContext().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });

                    AlertDialog alert = alertDialog.create();
                    alert.show();

                    Button ok = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                    ok.setTextColor(getResources().getColor(R.color.md_blue_500));
                    CredentialHelper.clearFields(email, password, confirmPassword);
                } else {
                    if(response.code() == 422) {
                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignUpActivity.this);
                        alertDialog.setTitle(R.string.error_email);
                        alertDialog.setCancelable(false);
                        alertDialog.setMessage(R.string.error_msg);
                        alertDialog.setPositiveButton(getApplicationContext().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });

                        alertDialog.setNeutralButton(getApplicationContext().getString(R.string.forgot_pass_activity), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(SignUpActivity.this, ForgotPasswordActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });

                        AlertDialog alert = alertDialog.create();
                        alert.show();

                        Button ok = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                        ok.setTextColor(getResources().getColor(R.color.md_blue_500));
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                        builder.setTitle(response.code() + getApplicationContext().getString(R.string.error));
                        builder.setMessage(response.message())
                                .setCancelable(false)
                                .setPositiveButton(getApplicationContext().getString(R.string.ok), null);
                        AlertDialog alert = builder.create();
                        alert.show();
                        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                        pbutton.setTextColor(Color.BLUE);
                    }
                    // After the implementation of "Forgot Password" option we could create a xml layout for the dialog.
                    // Until then we need to add the buttons dynamically.
                }
                signUp.setEnabled(true);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                t.printStackTrace();
                if( t instanceof UnknownHostException) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                    builder.setTitle(getApplicationContext().getString(R.string.unknown_host_exception));
                    builder.setMessage(t.getMessage())
                            .setCancelable(false)
                            .setPositiveButton(getApplicationContext().getString(R.string.retry), null);
                    AlertDialog alert = builder.create();
                    alert.show();
                    Button ok = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                    ok.setTextColor(Color.RED);
                    signUp.setEnabled(true);
                    progressDialog.dismiss();
                } else {
                    Toast.makeText(SignUpActivity.this, getApplicationContext().getString(R.string.internet_connection_prompt), Toast.LENGTH_SHORT).show();
                    signUp.setEnabled(true);
                    progressDialog.dismiss();
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        CharSequence values[] = {email.getEditText().getText().toString(), password.getEditText().getText().toString(), confirmPassword.getEditText().getText().toString() };
        outState.putCharSequenceArray(Constant.SAVED_STATES, values);
        outState.putBoolean(Constant.SERVER,personalServer.isChecked());
    }

    @Override
    public void onBackPressed(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignUpActivity.this);
        alertDialog.setCancelable(false);
        alertDialog.setMessage(R.string.error_cancelling_signUp_process_text);
        alertDialog.setPositiveButton(getApplicationContext().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SignUpActivity.super.onBackPressed();
            }
        });
        alertDialog.setNegativeButton(getApplicationContext().getString(R.string.no),null);

        AlertDialog alert = alertDialog.create();
        alert.show();
        
        Button yes = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        Button no = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        yes.setTextColor(getResources().getColor(R.color.md_blue_500));
        no.setTextColor(getResources().getColor(R.color.md_red_500));
    }
}
