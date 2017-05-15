package org.fossasia.susi.ai.helper;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Patterns;

import org.fossasia.susi.ai.R;

import java.net.URL;
import java.util.regex.Pattern;


/**
 * Created by saurabh on 11/10/16.
 */

public class CredentialHelper {
    /**
     * combination of at least six characters for the moment to get more users instead of strong passwords
     */
    private static Pattern PASSWORD_PATTERN = Pattern.compile("^.{6,64}$");

    public static boolean isEmailValid(String email) {
        email = email.trim();
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isPasswordValid(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    public static void clearFields(TextInputLayout... layouts) {
        for (TextInputLayout inputLayout : layouts) {
            if (inputLayout.getEditText() != null) {
                inputLayout.getEditText().setText(null);
            }
            inputLayout.setError(null);
        }
    }

    public static boolean checkPasswordValid(TextInputLayout password, Context context) {
        if (password.getEditText() == null)
            throw new IllegalArgumentException("No Edittext hosted!");
        if (!isPasswordValid(password.getEditText().getText().toString())) {
            password.setError(context.getString(R.string.pass_validation_text));
            return false;
        } else {
            password.setError(null);
            return true;
        }
    }

    public static boolean isURLValid(TextInputLayout url, Context context) {
        if (url.getEditText() == null)
            throw new IllegalArgumentException("No Edittext hosted!");
        if (!Patterns.WEB_URL.matcher(url.getEditText().getText().toString()).matches()) {
            url.setError("Invalid URL");
            return false;
        } else {
            url.setError(null);
            return true;
        }
    }

    public static String getValidURL(TextInputLayout url, Context context) {
        try {
            if (url.getEditText().getText().toString().trim().substring(0, 7).equals("http://") || url.getEditText().getText().toString().trim().substring(0, 8).equals("https://")) {
                URL susiURL = new URL(url.getEditText().getText().toString().trim());
                return susiURL.getProtocol() + "://" + susiURL.getHost();
            } else {
                URL susiURL = new URL("http://" + url.getEditText().getText().toString().trim());
                return susiURL.getProtocol() + "://" + susiURL.getHost();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean checkIfEmpty(TextInputLayout inputLayout, Context context) {
        if (inputLayout.getEditText() == null)
            throw new IllegalArgumentException("No Edittext hosted!");
        if (TextUtils.isEmpty(inputLayout.getEditText().getText().toString())) {
            inputLayout.setError(context.getString(R.string.field_cannot_be_empty));
            return true;
        } else {
            inputLayout.setError(null);
            return false;
        }
    }
}
