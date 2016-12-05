package org.fossasia.susi.ai.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.Toast;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.helper.Constant;
import org.fossasia.susi.ai.helper.CredentialHelper;
import org.fossasia.susi.ai.helper.PrefManager;
import org.fossasia.susi.ai.rest.ClientBuilder;
import org.fossasia.susi.ai.rest.model.LoginResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.email)
    TextInputLayout email;
    @BindView(R.id.password)
    TextInputLayout password;
    @BindView(R.id.log_in)
    Button logIn;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        Intent intent;
        if (!PrefManager.hasTokenExpired()) {
            intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        if (savedInstanceState != null) {
            email.getEditText().setText(savedInstanceState.getCharSequenceArray("savedStates")[0].toString());
            password.getEditText().setText(savedInstanceState.getCharSequenceArray("savedStates")[1].toString());

        }
    }
    
    @OnClick(R.id.sign_up)
    void signUp() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.log_in)
    void logIn() {
        if (CredentialHelper.checkIfEmpty(email, this) | CredentialHelper.checkIfEmpty(password, this)) {
            return;
        }
        if (!CredentialHelper.isEmailValid(email.getEditText().getText().toString())) {
            InvalidAcess();
            return;
        }
        email.setError(null);
        
        logIn.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.show();
        final Call<LoginResponse> authResponseCall = new ClientBuilder().getSusiApi()
                .login(email.getEditText().getText().toString().trim(),
                        password.getEditText().getText().toString());
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                authResponseCall.cancel();
                logIn.setEnabled(true);
            }
        });
        authResponseCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    //Save token and expiry date.
                    PrefManager.putString(Constant.ACCESS_TOKEN, response.body().getAccessToken());
                    long validity = System.currentTimeMillis() + response.body().getValidSeconds() * 1000;
                    PrefManager.putLong(Constant.TOKEN_VALIDITY, validity);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else
                    Toast.makeText(LoginActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                logIn.setEnabled(true);
                progressDialog.dismiss();


            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(LoginActivity.this, "Please check your internet.", Toast.LENGTH_SHORT).show();
                logIn.setEnabled(true);
                progressDialog.dismiss();
            }});}

            public void InvalidAcess(){
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage("The password you entered is incorrect. Please try again.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                return;
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(Color.BLUE);
            }

    @OnEditorAction(R.id.password_input)
    public boolean onEditorAction(int actionId) {
        boolean handled = false;
        if (actionId == EditorInfo.IME_ACTION_GO) {
            logIn();
            handled = true;
        }
        return handled;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        CharSequence values[] = {email.getEditText().getText().toString(), password.getEditText().getText().toString() };
        outState.putCharSequenceArray("savedStates", values);

    }
}


