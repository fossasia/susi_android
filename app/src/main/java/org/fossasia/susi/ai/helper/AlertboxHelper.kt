package org.fossasia.susi.ai.helper

import android.app.Activity
import android.content.DialogInterface
import android.support.v7.app.AlertDialog

/**
 * <h1>Helper class to display alert dialog boxes</h1>

 * Created by meeera on 18/6/17.
 */
class AlertboxHelper
/**
 * Instantiates a new Alertbox helper.

 * @param activity            the activity
 * *
 * @param title               the title
 * *
 * @param message             the message
 * *
 * @param dialogPositiveClick the dialog positive click
 * *
 * @param dialogNegativeClick the dialog negative click
 * *
 * @param positiveText        the positive text
 * *
 * @param negativeText        the negative text
 * *
 * @param colour              the colour
 */
(private val activity: Activity, private val title: String?, private val message: String?, private val dialogPositiveClick: DialogInterface.OnClickListener?, private val dialogNegativeClick: DialogInterface.OnClickListener?, private val positiveText: String?, private val negativeText: String?, private val colour: Int) {

    /**
     * Show alert box.
     */
    fun showAlertBox() {
        val alertDialog = AlertDialog.Builder(activity)
        alertDialog.setTitle(title)
        alertDialog.setCancelable(false)
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton(positiveText, dialogPositiveClick)
        alertDialog.setNeutralButton(negativeText, dialogNegativeClick)
        val alert = alertDialog.create()
        alert.show()
        val ok = alert.getButton(DialogInterface.BUTTON_POSITIVE)
        ok.setTextColor(colour)
    }
}
