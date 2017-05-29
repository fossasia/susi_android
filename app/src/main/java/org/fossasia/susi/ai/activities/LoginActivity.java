package org.fossasia.susi.ai.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.helper.Constant;
import org.fossasia.susi.ai.helper.CredentialHelper;
import org.fossasia.susi.ai.helper.PrefManager;
import org.fossasia.susi.ai.rest.ClientBuilder;
import org.fossasia.susi.ai.rest.model.LoginResponse;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
    @BindView(R.id.email_input)
    protected AutoCompleteTextView autoCompleteEmail;
    @BindView(R.id.password)
    TextInputLayout password;
    @BindView(R.id.log_in)
    Button logIn;
    @BindView(R.id.susi_default)
    protected RadioButton susiServer;
    @BindView(R.id.personal_server)
    protected RadioButton personalServer;
    @BindView(R.id.input_url)
    protected TextInputLayout url;

    private Set<String> savedEmails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        Intent intent;
        if (!PrefManager.hasTokenExpired()) {
            intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("FIRST_TIME", false);
            startActivity(intent);
            finish();
        }
        if (savedInstanceState != null) {
            email.getEditText().setText(savedInstanceState.getCharSequenceArray("savedStates")[0].toString());
            password.getEditText().setText(savedInstanceState.getCharSequenceArray("savedStates")[1].toString());
            if(savedInstanceState.getBoolean("server")) {
                url.setVisibility(View.VISIBLE);
            } else {
                url.setVisibility(View.GONE);
            }
        }
        savedEmails = new HashSet<>();
        if (PrefManager.getStringSet(Constant.SAVED_EMAIL) != null) {
            savedEmails.addAll(PrefManager.getStringSet(Constant.SAVED_EMAIL));
            autoCompleteEmail.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>(savedEmails)));
        }
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
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.forgot_password)
    public void forgotPassword() {
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.log_in)
    public void logIn() {
        if (CredentialHelper.checkIfEmpty(email, this) | CredentialHelper.checkIfEmpty(password, this)) {
            return;
        }
        if (!CredentialHelper.isEmailValid(email.getEditText().getText().toString())) {
            InvalidAcess();
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
        
        logIn.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Logging in...");
        progressDialog.show();

        final Call<LoginResponse> authResponseCall = new ClientBuilder().getSusiApi()
                .login(email.getEditText().getText().toString().trim().toLowerCase(),
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
                    // Save email for autocompletion
                    savedEmails.add(email.getEditText().getText().toString());
                    PrefManager.putStringSet(Constant.SAVED_EMAIL,savedEmails);
                    //Save token and expiry date.
                    PrefManager.putString(Constant.ACCESS_TOKEN, response.body().getAccessToken());
                    long validity = System.currentTimeMillis() + response.body().getValidSeconds() * 1000;
                    PrefManager.putLong(Constant.TOKEN_VALIDITY, validity);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("FIRST_TIME", true);
                    startActivity(intent);
                    finish();
                } else if(response.code() == 422) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setTitle(R.string.password_invalid_title);
                        builder.setMessage(R.string.password_invalid)
                                .setCancelable(false)
                                .setPositiveButton("OK", null);
                        AlertDialog alert = builder.create();
                        alert.show();
                        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                        pbutton.setTextColor(Color.BLUE);
                } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setTitle(response.code() + " Error");
                        builder.setMessage(response.message())
                                .setCancelable(false)
                                .setPositiveButton("OK", null);
                        AlertDialog alert = builder.create();
                        alert.show();
                        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                        pbutton.setTextColor(Color.BLUE);
                }
                logIn.setEnabled(true);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                t.printStackTrace();

                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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
                logIn.setEnabled(true);
                progressDialog.dismiss();
            }
        });
    }

    public void InvalidAcess(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle(R.string.email_invalid_title);
        builder.setMessage(R.string.email_invalid)
                .setCancelable(false)
                .setPositiveButton("RETRY", null);
        AlertDialog alert = builder.create();
        alert.show();
        Button ok = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        ok.setTextColor(Color.RED);
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
        outState.putBoolean("server",personalServer.isChecked());
    }
}