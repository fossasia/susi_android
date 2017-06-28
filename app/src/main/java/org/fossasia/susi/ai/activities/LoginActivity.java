package org.fossasia.susi.ai.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.helper.AlertboxHelper;
import org.fossasia.susi.ai.helper.Constant;
import org.fossasia.susi.ai.helper.CredentialHelper;
import org.fossasia.susi.ai.helper.PrefManager;
import org.fossasia.susi.ai.rest.ClientBuilder;
import org.fossasia.susi.ai.rest.responses.susi.LoginResponse;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * <h1>The Login activity.</h1>
 * <h2>This activity is used to login into the app.</h2>
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    @BindView(R.id.email)
    protected TextInputLayout email;
    @BindView(R.id.email_input)
    protected AutoCompleteTextView autoCompleteEmail;
    @BindView(R.id.password)
    protected TextInputLayout password;
    @BindView(R.id.log_in)
    protected Button logIn;
    @BindView(R.id.susi_default)
    protected RadioButton susiServer;
    @BindView(R.id.personal_server)
    protected RadioButton personalServer;
    @BindView(R.id.input_url)
    protected TextInputLayout url;

    private Set<String> savedEmails;
    private Realm realm;
    private String alertTitle,alertMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        Intent intent;
        realm = Realm.getDefaultInstance();
        if(!PrefManager.getBoolean(Constant.ANONYMOUS_LOGGED_IN, false)) {
            if (!PrefManager.hasTokenExpired()) {
                intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra(Constant.FIRST_TIME, false);
                startActivity(intent);
                finish();
            }
            if (savedInstanceState != null) {
                email.getEditText().setText(savedInstanceState.getCharSequenceArray(Constant.SAVED_STATES)[0].toString());
                password.getEditText().setText(savedInstanceState.getCharSequenceArray(Constant.SAVED_STATES)[1].toString());
                if (savedInstanceState.getBoolean(Constant.SERVER)) {
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
        } else if(PrefManager.getBoolean(Constant.ANONYMOUS_LOGGED_IN, false)) {
            intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra(Constant.FIRST_TIME, false);
            startActivity(intent);
            finish();
        }
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
     * Called when a user clicks on the sign up text view. Opens SignUpActivity
     */
    @OnClick(R.id.sign_up)
    public void signUp() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    /**
     * Called when a user clicks on the forgot password text view. Opens ForgotPasswordActivity
     */
    @OnClick(R.id.forgot_password)
    public void forgotPassword() {
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    /**
     * Called when a user clicks on the skip text view.
     */
    @OnClick(R.id.skip)
    public void skip() {
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        intent.putExtra(Constant.FIRST_TIME, false);
        PrefManager.clearToken();
        PrefManager.putBoolean(Constant.ANONYMOUS_LOGGED_IN, true);
        startActivity(intent);
        finish();
    }

    /**
     * Called when a user clicks on the login text view.
     */
    @OnClick(R.id.log_in)
    public void logIn() {
        if (CredentialHelper.checkIfEmpty(email, this) | CredentialHelper.checkIfEmpty(password, this)) {
            return;
        }
        if (!CredentialHelper.isEmailValid(email.getEditText().getText().toString())) {
            invalidAccess();
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
        logIn.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getApplicationContext().getString(R.string.login));
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

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.deleteAll();
                            Log.d(TAG, "execute: all messages deleted");
                        }
                    });

                    PrefManager.putBoolean(Constant.ANONYMOUS_LOGGED_IN, false);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Constant.FIRST_TIME, true);
                    startActivity(intent);
                    finish();
                } else if(response.code() == 422) {
                    alertTitle = getResources().getString(R.string.password_invalid_title);
                    alertMessage = getResources().getString(R.string.password_invalid);
                    alertNotSuccess(alertTitle, alertMessage);
                } else {
                    alertNotSuccess(response.code()+getResources().getString(R.string.error), response.message());
                }
                logIn.setEnabled(true);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                t.printStackTrace();
                if( t instanceof UnknownHostException) {
                    alertTitle = getResources().getString(R.string.unknown_host_exception);
                    alertMessage = t.getMessage();
                } else {
                    alertTitle = getResources().getString(R.string.error_internet_connectivity);
                    alertMessage = getResources().getString(R.string.no_internet_connection);
                }
                alertNotSuccess(alertTitle, alertMessage);
                logIn.setEnabled(true);
                progressDialog.dismiss();
            }
        });
    }

    /**
     * To check if the email and password entered by user are correct.
     */
    public void invalidAccess(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle(R.string.email_invalid_title);
        builder.setMessage(R.string.email_invalid)
                .setCancelable(false)
                .setPositiveButton(getApplicationContext().getString(R.string.retry), null);
        AlertDialog alert = builder.create();
        alert.show();
        Button ok = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        ok.setTextColor(Color.RED);
    }

    /**
     * On editor action boolean.
     *
     * @param actionId the action id
     * @return the boolean
     */
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
        outState.putCharSequenceArray(Constant.SAVED_STATES, values);
        outState.putBoolean(Constant.SERVER,personalServer.isChecked());
    }

    /**
     * Displays an alert dialog box in case of unsuccessful login.
     *
     * @param title   the title
     * @param message the message
     */
    public void alertNotSuccess(String title,String message) {
        AlertboxHelper notSuccessAlertboxHelper = new AlertboxHelper(LoginActivity.this, title, message, null, null, getResources().getString(R.string.ok), null, Color.BLUE);
        notSuccessAlertboxHelper.showAlertBox();
    }
}