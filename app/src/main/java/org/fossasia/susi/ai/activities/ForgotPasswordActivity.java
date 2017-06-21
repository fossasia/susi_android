package org.fossasia.susi.ai.activities;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.helper.Constant;
import org.fossasia.susi.ai.helper.AlertboxHelper;
import org.fossasia.susi.ai.helper.CredentialHelper;
import org.fossasia.susi.ai.helper.PrefManager;
import org.fossasia.susi.ai.rest.ClientBuilder;
import org.fossasia.susi.ai.rest.responses.susi.ForgotPasswordResponse;

import java.net.UnknownHostException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * <h1>The Forgot password activity.</h1>
 * <h2>This activity is used in case user forgets his/her password and wants to recover it.</h2>
 */
public class ForgotPasswordActivity extends AppCompatActivity {

    @BindView(R.id.forgot_email)
    protected TextInputLayout email;
    @BindView(R.id.reset_button)
    protected Button resetButton;
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
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);
        setTitle(getString(R.string.forgot_pass_activity));

        if (savedInstanceState != null) {
            email.getEditText().setText(savedInstanceState.getCharSequenceArray(Constant.SAVED_STATES)[0].toString());
            if(savedInstanceState.getBoolean(Constant.SERVER)) {
                url.setVisibility(View.VISIBLE);
            } else {
                url.setVisibility(View.GONE);
            }
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
     * Called when a user clicks on the reset up button
     */
    @OnClick(R.id.reset_button)
    public void signUp() {
        if (CredentialHelper.checkIfEmpty(email, this)) {
            return;
        }

        if (!CredentialHelper.isEmailValid(email.getEditText().getText().toString())) {
            email.setError(getString(R.string.email_invalid_title));
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

        email.setError(null);
        resetButton.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getApplicationContext().getString(R.string.loading));
        progressDialog.show();

        final Call<ForgotPasswordResponse> forgotPasswordResponseCall = new ClientBuilder().getSusiApi()
                .forgotPassword(email.getEditText().getText().toString().trim());
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                forgotPasswordResponseCall.cancel();
                resetButton.setEnabled(true);
            }
        });

        forgotPasswordResponseCall.enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    alertSuccess(response);
                } else if(response.code() == 422) {
                    alertTitle = getResources().getString(R.string.email_invalid_title);
                    alertMessage = getResources().getString(R.string.email_invalid);
                    alertNotSuccess(alertTitle, alertMessage, getResources().getString(R.string.retry), Color.RED);
                } else {
                    alertNotSuccess(response.code()+getResources().getString(R.string.error), response.message(), getResources().getString(R.string.ok), Color.BLUE);
                }
                resetButton.setEnabled(true);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                t.printStackTrace();
                if(t instanceof UnknownHostException) {
                    alertTitle = getResources().getString(R.string.unknown_host_exception);
                    alertMessage = t.getMessage();
                } else {
                    alertTitle = getResources().getString(R.string.error_internet_connectivity);
                    alertMessage = getResources().getString(R.string.no_internet_connection);
                }
                alertNotSuccess(alertTitle,alertMessage, getResources().getString(R.string.retry), Color.RED);
                resetButton.setEnabled(true);
                progressDialog.dismiss();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        CharSequence values[] = {email.getEditText().getText().toString()};
        outState.putCharSequenceArray(Constant.SAVED_STATES, values);
        outState.putBoolean(Constant.SERVER,personalServer.isChecked());
    }

    /**
     * Displays an alert dialog box in case of successful reset of password.
     *
     * @param response the response from server
     */
    public void alertSuccess(Response<ForgotPasswordResponse> response) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        };
        alertTitle = getResources().getString(R.string.forgot_password_mail_sent);
        alertMessage = response.body().getMessage();
        AlertboxHelper successAlertboxHelper = new AlertboxHelper(ForgotPasswordActivity.this, alertTitle, alertMessage, dialogClickListener, null, getResources().getString(R.string.Continue), null, Color.BLUE);
        successAlertboxHelper.showAlertBox();
    }

    /**
     * Displays an alert dialog box in case of unsuccessful reset of password.
     *
     * @param title              the title of dialog box
     * @param message            the body of dialog box
     * @param positiveButtonText the positive button text
     * @param colour             the colour of buttons
     */
    public void alertNotSuccess(String title,String message,String positiveButtonText,int colour) {
        AlertboxHelper notSuccessAlertboxHelper = new AlertboxHelper(ForgotPasswordActivity.this, title, message, null, null, positiveButtonText, null, colour);
        notSuccessAlertboxHelper.showAlertBox();
    }
}
