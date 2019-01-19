package org.fossasia.susi.ai.device

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.device.deviceconnect.DeviceConnectFragment

/*
*   Created by batbrain7 on 20/06/18
*   Device activity is where we can setup a new device and also
*   view the devices currently connected
 */

class DeviceActivity : AppCompatActivity() {

    private val TAG_DEVICE_CONNECT_FRAGMENT = "DeviceConnectFragment"
    private val TAG_DEVICE_DETAILS_FRAGMENT = "DeviceDetailsFragment"

    lateinit var mainWifi: WifiManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out)
        setContentView(R.layout.activity_device)

        mainWifi = application.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val deviceConnectFragment = DeviceConnectFragment()
        supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, deviceConnectFragment, TAG_DEVICE_CONNECT_FRAGMENT)
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
        finish()
        super.onBackPressed()
    }
}
