package org.fossasia.susi.ai.settings

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.login.LoginActivity
import android.content.Intent
import android.util.Log
import android.view.MenuItem
import org.fossasia.susi.ai.chat.ChatActivity
import org.fossasia.susi.ai.settings.contract.ISettingsPresenter
import org.fossasia.susi.ai.settings.contract.ISettingsView

/**
 * <h1>The Settings activity.</h1>
 * <h2>This activity is used to set Preferences into the app.</h2>
 *
 * Created by mayanktripathi on 07/07/17.
 */

class SettingsActivity : AppCompatActivity(), ISettingsView {

    var settingsPresenter: ISettingsPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out)
        setContentView(R.layout.activity_settings)
    }

    override fun showAlert(activity: FragmentActivity) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(Constant.CHANGE_SERVER)
        builder.setMessage(Constant.SERVER_CHANGE_PROMPT)
                .setCancelable(false)
                .setNegativeButton(Constant.CANCEL, null)
                .setPositiveButton(activity.getString(R.string.ok)) { dialog, _ ->
                    settingsPresenter?.deleteMsg()
                    val intent = Intent(activity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    activity.startActivity(intent)
                    dialog.dismiss()
                }
        val alert = builder.create()
        alert.show()
    }

    fun exitActivity() {
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out)
        val intent = Intent(this@SettingsActivity, ChatActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        exitActivity()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> { finish()
                exitActivity()
                Log.v("chirag","chirag") }
        }
        return super.onOptionsItemSelected(item);
    }
}
