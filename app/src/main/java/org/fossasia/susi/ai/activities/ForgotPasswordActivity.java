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

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.helper.CredentialHelper;
import org.fossasia.susi.ai.helper.PrefManager;
import org.fossasia.susi.ai.rest.ClientBuilder;
import org.fossasia.susi.ai.rest.model.ForgotPasswordResponse;

import java.net.UnknownHostException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);
        setTitle(getString(R.string.forgot_pass_activity));

        if (savedInstanceState != null) {
            email.getEditText().setText(savedInstanceState.getCharSequenceArray("savedStates")[0].toString());
            if(savedInstanceState.getBoolean("server")) {
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

    @OnClick(R.id.personal_server)
    public void showURL() {
        url.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.susi_default)
    public void hideURL() {
        url.setVisibility(View.GONE);
    }

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
                    PrefManager.putBoolean("is_susi_server_selected", false);
                    PrefManager.putString("custom_server", CredentialHelper.getValidURL(url, this));
                } else {
                    url.setError("Invalid URL");
                    return;
                }
            } else {
                return;
            }
        } else{
            PrefManager.putBoolean("is_susi_server_selected", true);
        }

        email.setError(null);
        resetButton.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
                    builder.setMessage(response.body().getMessage())
                            .setCancelable(false)
                            .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                    Button continueButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                    continueButton.setTextColor(Color.BLUE);

                } else if(response.code() == 422) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
                    builder.setTitle(R.string.email_invalid_title);
                    builder.setMessage(R.string.email_invalid)
                            .setCancelable(false)
                            .setPositiveButton("Retry", null);
                    AlertDialog alert = builder.create();
                    alert.show();
                    Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbutton.setTextColor(Color.RED);

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
                    builder.setTitle(response.code() + " Error");
                    builder.setMessage(response.message())
                            .setCancelable(false)
                            .setPositiveButton("OK", null);
                    AlertDialog alert = builder.create();
                    alert.show();
                    Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbutton.setTextColor(Color.BLUE);
                }
                resetButton.setEnabled(true);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                t.printStackTrace();

                AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
                if( t instanceof UnknownHostException) {
                    builder.setTitle("Unknown Host Exception");
                    builder.setMessage(t.getMessage());
                } else {
                    builder.setTitle(R.string.error_internet_connectivity);
                    builder.setMessage(R.string.no_internet_connection);
                }
                builder.setCancelable(false)
                        .setPositiveButton("RETRY", null);
                AlertDialog alert = builder.create();
                alert.show();
                Button ok = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                ok.setTextColor(Color.RED);
                resetButton.setEnabled(true);
                progressDialog.dismiss();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        CharSequence values[] = {email.getEditText().getText().toString()};
        outState.putCharSequenceArray("savedStates", values);
        outState.putBoolean("server",personalServer.isChecked());
    }
}
