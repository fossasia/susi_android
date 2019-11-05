package org.fossasia.susi.ai.device

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.device.connecteddevices.ConnectedDeviceFragment
import org.fossasia.susi.ai.device.deviceconnect.DeviceConnectFragment
import org.fossasia.susi.ai.device.viewdevice.ViewDeviceFragment

/*
*   Created by batbrain7 on 20/06/18
*   Device activity is where we can setup a new device and also
*   view the devices currently connected
 */

class DeviceActivity : AppCompatActivity() {

    lateinit var mainWifi: WifiManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out)
        setContentView(R.layout.activity_device)

        mainWifi = application.getSystemService(Context.WIFI_SERVICE) as WifiManager

        if (intent.getStringExtra(CONNECT_TO) == TAG_DEVICE_CONNECT_FRAGMENT) {
            val deviceConnectFragment = DeviceConnectFragment()
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, deviceConnectFragment, TAG_DEVICE_CONNECT_FRAGMENT)
                    .commit()
        } else if (intent.getStringExtra(CONNECT_TO) == TAG_VIEW_DEVICE_FRAGMENT) {
            val viewDeviceFragment = ViewDeviceFragment()
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, viewDeviceFragment, TAG_VIEW_DEVICE_FRAGMENT)
                    .commit()
        } else {
            val connectedDeviceFragment = ConnectedDeviceFragment()
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, connectedDeviceFragment, TAG_CONNECTED_DEVICE_FRAGMNENT)
                    .commit()
        }
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

    companion object {
        const val TAG_DEVICE_CONNECT_FRAGMENT = "DeviceConnectFragment"
        const val TAG_CONNECTED_DEVICE_FRAGMNENT = "ConnectedDeviceFragment"
        const val TAG_VIEW_DEVICE_FRAGMENT = "ViewDeviceFragment"
        const val DEVICE_DETAILS = "deviceDetails"
        const val MAC_ID = "macId"
        const val CONNECT_TO = "connect_to"
        lateinit var macId: String
        var ANONYMOUS_MODE: Boolean = false
    }
}
