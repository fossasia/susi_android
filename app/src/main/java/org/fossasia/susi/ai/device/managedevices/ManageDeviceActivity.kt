package org.fossasia.susi.ai.device.managedevices

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import org.fossasia.susi.ai.R

class ManageDeviceActivity : AppCompatActivity() {

    private val TAG_MUSIC_SERVERS_FRAGMNENT = "MusicServersFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out)
        setContentView(R.layout.activity_manage_device)

        val musicServerFragment = MusicServersFragment()
        supportFragmentManager.beginTransaction()
                .replace(R.id.manage_device_container, musicServerFragment, TAG_MUSIC_SERVERS_FRAGMNENT)
                .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out)
        super.onBackPressed()
    }
}
