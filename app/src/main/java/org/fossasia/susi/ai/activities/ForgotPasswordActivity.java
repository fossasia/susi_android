package org.fossasia.susi.ai.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.helper.CredentialHelper;
import org.fossasia.susi.ai.rest.ClientBuilder;
import org.fossasia.susi.ai.rest.model.ForgotPasswordResponse;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);
        setTitle(getString(R.string.forgot_pass_activity));

        if (savedInstanceState != null) {
            email.getEditText().setText(savedInstanceState.getCharSequenceArray("savedStates")[0].toString());
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

    @OnClick(R.id.reset_button)
    public void signUp() {
        final LinearLayout linearLayout = (LinearLayout) ForgotPasswordActivity.this.findViewById(R.id.activity_forgot_password);
        if (CredentialHelper.checkIfEmpty(email, this)) {
            return;
        }
        if (!CredentialHelper.isEmailValid(email.getEditText().getText().toString())) {
            email.setError(getString(R.string.email_invalid_title));
            return;
        }
        email.setError(null);
        resetButton.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(this);
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
                    Snackbar snackbar = Snackbar
                            .make(linearLayout, response.body().getMessage(), Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("Continue", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ForgotPasswordActivity.this, ChangePassword.class);
                            startActivity(intent);
                        }
                    });
                    snackbar.show();
                    snackbar.setActionTextColor(Color.BLUE);

                } else {
                    Snackbar snackbar = Snackbar
                            .make(linearLayout, "", Snackbar.LENGTH_INDEFINITE);
                    snackbar.show();
                }
                resetButton.setEnabled(true);
                progressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                t.printStackTrace();
                Snackbar snackbar = Snackbar
                        .make(linearLayout, R.string.error_internet_connectivity, Snackbar.LENGTH_LONG);
                snackbar.show();

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
    }
}

