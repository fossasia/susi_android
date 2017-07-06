package org.fossasia.susi.ai.helper;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import org.fossasia.susi.ai.R;

import java.net.URL;
import java.util.regex.Pattern;

/**
 * <h1>Helper class to verify credentials of user during login and sign up.</h1>
 *
 * Created by saurabh on 11/10/16.
 */
public class CredentialHelper {

    private static final String TAG = "CredentialHelper";

    private static Pattern PASSWORD_PATTERN = Pattern.compile("^.{6,64}$");

    /**
     * Is email valid boolean.
     *
     * @param mail the email
     * @return the boolean
     */
    public static boolean isEmailValid(String mail) {
        Log.d(TAG, "isEmailValid: " + mail);
        String email = mail.trim();
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Is password valid boolean.
     *
     * @param password the password
     * @return the boolean
     */
    public static boolean isPasswordValid(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Clear fields.
     *
     * @param layouts the layouts
     */
    public static void clearFields(TextInputLayout... layouts) {
        for (TextInputLayout inputLayout : layouts) {
            if (inputLayout.getEditText() != null) {
                inputLayout.getEditText().setText(null);
            }
            inputLayout.setError(null);
        }
    }

    /**
     * Is url valid boolean.
     *
     * @param url     the url
     * @return the boolean
     */
    public static boolean isURLValid(String url) {
        return Patterns.WEB_URL.matcher(url).matches();
    }

    /**
     * Gets valid url.
     *
     * @param url     the url
     * @return the valid url
     */
    public static String getValidURL(String url) {
        try {
            if (url.trim().substring(0, 7).equals("http://") || url.trim().substring(0, 8).equals("https://")) {
                URL susiURL = new URL(url.trim());
                return susiURL.getProtocol() + "://" + susiURL.getHost();
            } else {
                URL susiURL = new URL("http://" + url.trim());
                return susiURL.getProtocol() + "://" + susiURL.getHost();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Check if empty boolean.
     *
     * @param inputLayout the input layout
     * @param context     the context
     * @return the boolean
     */
    public static boolean checkIfEmpty(TextInputLayout inputLayout, Context context) {
        if (TextUtils.isEmpty(inputLayout.getEditText().getText().toString())) {
            inputLayout.setError(context.getString(R.string.field_cannot_be_empty));
            return true;
        } else {
            inputLayout.setError(null);
            return false;
        }
    }
}
