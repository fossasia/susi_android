package org.fossasia.susi.ai.activities.SignUp;

import android.app.ProgressDialog;
import android.widget.ProgressBar;

/**
 * Created by mayanktripathi on 04/07/17.
 */

public interface SignUpView {

    void alertSuccess();

    void alertFailure();

    void alertError(String title,String message);

    void setErrorEmail(String msg);

    void setErrorPass(String msg);

    void setErrorConpass(String msg);

    void setErrorUrl(String msg);

    void enableSignUp(boolean bool);

    boolean isPersonalServer();

    void clearFiled();

    boolean checkIfEmptyUrl();

    boolean isURLValid();

    boolean isEmailValid(String email);

    void setupPasswordWatcher();

    String getValidURL();

    ProgressDialog showProcess();

    boolean checkPasswordValid();

    boolean checkCredentials();

}
