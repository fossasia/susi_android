package org.fossasia.susi.ai.activities.SignUp;

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

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.activities.ForgotPasswordActivity;
import org.fossasia.susi.ai.activities.LoginActivity;
import org.fossasia.susi.ai.helper.Constant;
import org.fossasia.susi.ai.helper.CredentialHelper;
import org.fossasia.susi.ai.helper.AlertboxHelper;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SignUpActivity extends AppCompatActivity implements SignUpView{

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
    SignUpInteractor signUpInteractor;


    private static final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        signUpInteractor = new SignUpPresenterImpl(this, getApplicationContext());


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

    @Override
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

    @Override
    public void alertError(String title,String message) {
        AlertboxHelper errorAlertboxHelper = new AlertboxHelper(SignUpActivity.this, title, message, null, null, getResources().getString(R.string.ok), null, Color.BLUE);
        errorAlertboxHelper.showAlertBox();
    }

    @Override
    public void setErrorEmail(String msg) {
        email.setError(msg);
    }

    @Override
    public void setErrorPass(String msg) {
        password.setError(msg);
    }

    @Override
    public void setErrorConpass(String msg) {
        confirmPassword.setError(msg);
    }

    @Override
    public void setErrorUrl(String msg) {
        url.setError(msg);
    }

    @Override
    public void enableSignUp(boolean bool) {
        signUp.setEnabled(bool);
    }

    @Override
    public boolean isPersonalServer() {
        return personalServer.isChecked();
    }

    @Override
    public void clearFiled() {
        CredentialHelper.clearFields(email, password, confirmPassword);
    }

    @Override
    public boolean checkIfEmptyUrl() {
        return CredentialHelper.checkIfEmpty(url,this);
    }

    @Override
    public boolean isURLValid() {
        return CredentialHelper.isURLValid(url,this);
    }

    @Override
    public boolean isEmailValid(String email) {
        return CredentialHelper.isEmailValid(email);
    }

    @Override
    public void setupPasswordWatcher() {
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    CredentialHelper.checkPasswordValid(password, SignUpActivity.this);
                }
            }
        });
    }

    @Override
    public String getValidURL() {
        return CredentialHelper.getValidURL(url, this);
    }

    @Override
    public ProgressDialog showProcess() {
        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getApplicationContext().getString(R.string.signing_up));
        progressDialog.show();
        return progressDialog;
    }

    @Override
    public boolean checkPasswordValid() {
        return CredentialHelper.checkPasswordValid(password, this);
    }

    @Override
    public boolean checkCredentials() {

        return CredentialHelper.checkIfEmpty(email, this) |
                CredentialHelper.checkIfEmpty(password, this) |
                CredentialHelper.checkIfEmpty(confirmPassword, this);
    }

    @Override
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

    @OnClick(R.id.sign_up)
    public void signUp() {
            signUpInteractor.signUp(email.getEditText().getText().toString() , password.getEditText().getText().toString(), confirmPassword.getEditText().getText().toString());
        }

    @OnClick(R.id.personal_server)
    public void showURL() {
        url.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.susi_default)
    public void hideURL() {
        url.setVisibility(View.GONE);
    }

}
