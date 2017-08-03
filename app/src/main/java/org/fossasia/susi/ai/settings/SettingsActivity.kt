package org.fossasia.susi.ai.settings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.fossasia.susi.ai.R
import android.content.Intent
import android.view.MenuItem
import org.fossasia.susi.ai.chat.ChatActivity

/**
 * <h1>The Settings activity.</h1>
 * <h2>This activity is used to set Preferences into the app.</h2>
 *
 * Created by mayanktripathi on 07/07/17.
 */

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out)
        setContentView(R.layout.activity_settings)
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
            android.R.id.home -> {
                finish()
                exitActivity()
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
