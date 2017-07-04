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
import org.fossasia.susi.ai.helper.AlertboxHelper;
import org.fossasia.susi.ai.helper.PrefManager;
import org.fossasia.susi.ai.login.LoginActivity;
import org.fossasia.susi.ai.rest.ClientBuilder;
import org.fossasia.susi.ai.rest.responses.susi.SignUpResponse;

import java.net.UnknownHostException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * <h1>The SignUp activity.</h1>
 * <h2>This activity is used to sign up into the app.</h2>
 */
public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.email)
    protected TextInputLayout email;
    @BindView(R.id.password)
    protected TextInputLayout password;
    @BindView(R.id.confirm_password)
    protected TextInputLayout confirmPassword;
    @BindView(R.id.sign_up)
    protected Button signUp;
    @BindView(R.id.susi_default)
    protected RadioButton susiServer;
    @BindView(R.id.personal_server)
    protected RadioButton personalServer;
    @BindView(R.id.input_url)
    protected TextInputLayout url;

    private String alertTitle,alertMessage;

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

    /**
     * Show input edit text for custom url.
     */
    @OnClick(R.id.personal_server)
    public void showURL() {
        url.setVisibility(View.VISIBLE);
    }

    /**
     * Hide input edit text for custom url.
     */
    @OnClick(R.id.susi_default)
    public void hideURL() {
        url.setVisibility(View.GONE);
    }

    /**
     * Called when a user clicks on the sign up button
     */
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
                if (CredentialHelper.getValidURL(url.getEditText().getText().toString()) != null) {
                    PrefManager.putBoolean(Constant.SUSI_SERVER, false);
                    PrefManager.putString(Constant.CUSTOM_SERVER, CredentialHelper.getValidURL(url.getEditText().getText().toString()));
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
                    alertSuccess();
                    CredentialHelper.clearFields(email, password, confirmPassword);
                } else {
                    if(response.code() == 422) {
                        alertFailure();
                    } else {
                        alertError(response.code()+getResources().getString(R.string.error), response.message());
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
                    alertError(getResources().getString(R.string.unknown_host_exception), t.getMessage());
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

    /**
     * Displays an alert dialog box in case of successful sign up
     */
    public void alertSuccess() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        };
        alertTitle = getResources().getString(R.string.signup);
        alertMessage = getResources().getString(R.string.signup_msg);
        AlertboxHelper successAlertboxHelper = new AlertboxHelper(SignUpActivity.this, alertTitle, alertMessage, dialogClickListener, null, getResources().getString(R.string.ok), null, getResources().getColor(R.color.md_blue_500));
        successAlertboxHelper.showAlertBox();
    }

    /**
     * Displays an alert dialog box in case of unsuccessful sign up. Shows error while signing up.
     *
     * @param title   the title
     * @param message the message
     */
    public void alertError(String title,String message) {
        AlertboxHelper errorAlertboxHelper = new AlertboxHelper(SignUpActivity.this, title, message, null, null, getResources().getString(R.string.ok), null, Color.BLUE);
        errorAlertboxHelper.showAlertBox();
    }

    /**
     * Displays an alert dialog box in case of unsuccessful sign up or any exception
     */
    public void alertFailure() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        };
        DialogInterface.OnClickListener dialogClickListenern = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(SignUpActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        };
        alertTitle = getResources().getString(R.string.error_email);
        alertMessage = getResources().getString(R.string.error_msg);
        AlertboxHelper failureAlertboxHelper = new AlertboxHelper(SignUpActivity.this, alertTitle, alertMessage, dialogClickListener, dialogClickListenern, getResources().getString(R.string.ok), getResources().getString(R.string.forgot_pass_activity), getResources().getColor(R.color.md_blue_500));
        failureAlertboxHelper.showAlertBox();
    }
}
