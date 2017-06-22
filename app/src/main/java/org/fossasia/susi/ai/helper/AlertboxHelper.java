package org.fossasia.susi.ai.helper;
import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Button;

/**
 * <h1>Helper class to display alert dialog boxes</h1>
 *
 * Created by meeera on 18/6/17.
 */
public class AlertboxHelper {
    private Activity activity;
    private String title, message, positiveText, negativeText;
    private DialogInterface.OnClickListener dialogPositiveClick, dialogNegativeClick;
    private int colour;

    /**
     * Instantiates a new Alertbox helper.
     *
     * @param activity            the activity
     * @param title               the title
     * @param message             the message
     * @param dialogPositiveClick the dialog positive click
     * @param dialogNegativeClick the dialog negative click
     * @param positiveText        the positive text
     * @param negativeText        the negative text
     * @param colour              the colour
     */
    public AlertboxHelper(Activity activity, String title, String message, DialogInterface.OnClickListener dialogPositiveClick, DialogInterface.OnClickListener dialogNegativeClick, String positiveText, String negativeText, int colour) {
        this.activity = activity;
        this.title = title;
        this.message = message;
        this.dialogPositiveClick = dialogPositiveClick;
        this.dialogNegativeClick = dialogNegativeClick;
        this.positiveText = positiveText;
        this.negativeText = negativeText;
        this.colour = colour;
    }

    /**
     * Show alert box.
     */
    public void showAlertBox() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle(title);
        alertDialog.setCancelable(false);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(positiveText, dialogPositiveClick);
        alertDialog.setNeutralButton(negativeText, dialogNegativeClick);
        AlertDialog alert = alertDialog.create();
        alert.show();
        Button ok = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        ok.setTextColor(colour);
    }
}
