package org.fossasia.susi.ai.device

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.device.contract.IDeviceView


class DeviceActivity : AppCompatActivity(), IDeviceView {

    val TAG: String = DeviceActivity::class.java.name
    lateinit var devicePresenter: DevicePresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out)
        setContentView(R.layout.activity_device)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out)
        super.onBackPressed()
    }

    override fun onDeviceConnectedSuccess() {
        Toast.makeText(this, R.string.connect_success, Toast.LENGTH_SHORT).show()
    }

    override fun showProgress() {
    }

    override fun onDeviceConnectionError() {
    }

    fun chooseWifi(view: View) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle(R.string.device_hosted_connection)
        builder.setMessage(R.string.choose_wifi)
        builder.setPositiveButton(R.string.ok) { dialog, which ->
            val intent = Intent(Intent.ACTION_MAIN, null)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            val componentName = ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings")
            intent.component = componentName
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        builder.setNegativeButton(R.string.cancel) { dialog, which ->
            dialog.dismiss()
        }

        builder.create().show()
    }
}
