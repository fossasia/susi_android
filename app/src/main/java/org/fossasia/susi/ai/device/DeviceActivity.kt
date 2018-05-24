package org.fossasia.susi.ai.device

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.MenuItem
import android.view.View
import org.fossasia.susi.ai.BuildConfig
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.device.contract.IDeviceView
import android.content.Intent
import android.content.ComponentName




class DeviceActivity : AppCompatActivity(),IDeviceView {

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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showProgress() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDeviceConnectionError() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun chooseWifi(view: View) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle(R.string.device_hosted_connection)
        builder.setMessage(R.string.choose_wifi)
        builder.setPositiveButton("OK"){dialog, which ->
            val intent = Intent(Intent.ACTION_MAIN, null)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            val cn = ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings")
            intent.component = cn
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        builder.setNegativeButton("CANCEL"){dialog,which ->
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}
