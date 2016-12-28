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

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.helper.CredentialHelper;
import org.fossasia.susi.ai.rest.ClientBuilder;
import org.fossasia.susi.ai.rest.model.ResetPasswordResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    @BindView(R.id.token_id)
    protected TextInputLayout token;
    @BindView(R.id.password)
    protected TextInputLayout password;
    @BindView(R.id.confirm_password)
    protected TextInputLayout confirmPassword;
    @BindView(R.id.reset_password)
    protected Button resetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        setTitle(getString(R.string.reset_pass_activity));
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            token.getEditText().setText(savedInstanceState.getCharSequenceArray("savedStates")[0].toString());
            password.getEditText().setText(savedInstanceState.getCharSequenceArray("savedStates")[1].toString());
            confirmPassword.getEditText().setText(savedInstanceState.getCharSequenceArray("savedStates")[2].toString());
        }

        passwordChecker();
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

    private void passwordChecker() {
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    CredentialHelper.checkPasswordValid(password, ChangePasswordActivity.this);
                }
            }
        });
    }

    @OnClick(R.id.reset_password)
    public void resetPassword() {
        if (CredentialHelper.checkIfEmpty(token, this) |
                CredentialHelper.checkIfEmpty(password, this) |
                CredentialHelper.checkIfEmpty(confirmPassword, this)) {
            return;
        }
        if (!CredentialHelper.checkPasswordValid(password, this)) {
            return;
        }
        if (!password.getEditText().getText().toString()
                .equals(confirmPassword.getEditText().getText().toString())) {
            confirmPassword.setError(getString(R.string.error_password_matching));
            return;
        }
        confirmPassword.setError(null);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.pass_change_dialog));
        progressDialog.show();
        final Call<ResetPasswordResponse> resetPasswordResponseCall = new ClientBuilder().getSusiApi()
                .resetPassword(token.getEditText().getText().toString().trim(),
                        password.getEditText().getText().toString());
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                resetPasswordResponseCall.cancel();
                resetPassword.setEnabled(true);
            }
        });
        resetPasswordResponseCall.enqueue(new Callback<ResetPasswordResponse>() {
            @Override
            public void onResponse(Call<ResetPasswordResponse> call, Response<ResetPasswordResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChangePasswordActivity.this);
                    alertDialog.setTitle("Password Changed!");
                    alertDialog.setCancelable(false);
                    alertDialog.setMessage(response.body().getMessageDetails());
                    alertDialog.setPositiveButton("Log In", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    });
                    AlertDialog alert = alertDialog.create();
                    alert.show();
                    Button logIn = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                    logIn.setTextColor(getResources().getColor(R.color.md_blue_500));

                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChangePasswordActivity.this);
                    alertDialog.setCancelable(false);
                    alertDialog.setTitle(getString(R.string.error_invalid_token));
                    alertDialog.setMessage(getString(R.string.email_token_string_message));
                    alertDialog.setPositiveButton("Retry", null);
                    AlertDialog alert = alertDialog.create();
                    alert.show();
                    Button retry = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                    retry.setTextColor(getResources().getColor(R.color.md_red_500));
                }

                resetPassword.setEnabled(true);
                progressDialog.dismiss();
                CredentialHelper.clearFields(token);
            }

            @Override
            public void onFailure(Call<ResetPasswordResponse> call, Throwable t) {
                t.printStackTrace();
                AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this);
                builder.setTitle(R.string.error_internet_connectivity);
                builder.setMessage(R.string.no_internet_connection)
                        .setCancelable(false)
                        .setPositiveButton("OK", null);
                AlertDialog alert = builder.create();
                alert.show();
                Button ok = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                ok.setTextColor(Color.BLUE);
                resetPassword.setEnabled(true);
                progressDialog.dismiss();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        CharSequence values[] = {token.getEditText().getText().toString(), password.getEditText().getText().toString(), confirmPassword.getEditText().getText().toString()};
        outState.putCharSequenceArray("savedStates", values);
    }


}
