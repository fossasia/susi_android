package org.fossasia.susi.ai.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.helper.CredentialHelper;
import org.fossasia.susi.ai.rest.ClientBuilder;
import org.fossasia.susi.ai.rest.model.SignUpResponse;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        if(savedInstanceState!=null){
            email.getEditText().setText(savedInstanceState.getCharSequenceArray("savedStates")[0].toString());
            password.getEditText().setText(savedInstanceState.getCharSequenceArray("savedStates")[1].toString());
            confirmPassword.getEditText().setText(savedInstanceState.getCharSequenceArray("savedStates")[2].toString());
        }

        setupPasswordWatcher();
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

    @OnClick(R.id.sign_up)
    void signUp() {
        if (CredentialHelper.checkIfEmpty(email, this) |
                CredentialHelper.checkIfEmpty(password, this) |
                CredentialHelper.checkIfEmpty(confirmPassword, this)) {
            return;
        }
        if (!CredentialHelper.isEmailValid(email.getEditText().getText().toString())) {
            email.setError("Invalid email");
            return;
        }
        email.setError(null);
        if (!CredentialHelper.checkPasswordValid(password, this)) {
            return;
        }
        if (!password.getEditText().getText().toString()
                .equals(confirmPassword.getEditText().getText().toString())) {
            confirmPassword.setError("Passwords do not match");
            return;
        }
        confirmPassword.setError(null);
        signUp.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing up...");
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
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });

                    AlertDialog alert = alertDialog.create();
                    alert.show();


                    Button ok = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                    ok.setTextColor(getResources().getColor(R.color.md_blue_500));

                } else
                    Toast.makeText(SignUpActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                signUp.setEnabled(true);
                progressDialog.dismiss();
                CredentialHelper.clearFields(email, password, confirmPassword);

            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(SignUpActivity.this, "Please check your internet.", Toast.LENGTH_SHORT).show();
                signUp.setEnabled(true);
                progressDialog.dismiss();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        CharSequence values[] = {email.getEditText().getText().toString(), password.getEditText().getText().toString(), confirmPassword.getEditText().getText().toString() };
        outState.putCharSequenceArray("savedStates", values);
    }
}

