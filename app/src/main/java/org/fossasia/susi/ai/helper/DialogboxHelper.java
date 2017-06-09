package org.fossasia.susi.ai.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
//import android.widget.Button;

/**
 * Created by mayanktripathi on 09/06/17.
 */

public class DialogboxHelper {

    private DialogInterface.OnClickListener dialogPositiveClick,dialogNegativeClick;

    public void onSuccess(Activity activity, String title, String message){

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle(title);
        alertDialog.setCancelable(false);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Yes", dialogPositiveClick);
        alertDialog.setNeutralButton("No", dialogNegativeClick);

        AlertDialog alert = alertDialog.create();
        alert.show();

        //Button ok = alert.getButton(DialogInterface.BUTTON_POSITIVE);
    }

    public void onFailure(Activity activity, String title, String message){

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle(title);
        alertDialog.setCancelable(false);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Ok", dialogPositiveClick);

        AlertDialog alert = alertDialog.create();
        alert.show();

        //Button ok = alert.getButton(DialogInterface.BUTTON_POSITIVE);
    }



}
