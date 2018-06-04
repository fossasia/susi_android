package org.fossasia.susi.ai.device


import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_device.*
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.device.contract.IDeviceView


class DeviceActivity : AppCompatActivity(), IDeviceView {

    lateinit var devicePresenter: DevicePresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out)
        setContentView(R.layout.activity_device)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
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

        noDeviceFound.visibility = View.GONE
        deviceTutorial.visibility = View.GONE

        Handler().postDelayed({
            scanDevice.visibility = View.GONE
            scanProgress.visibility = View.GONE
            noDeviceFound.visibility = View.VISIBLE
            deviceTutorial.visibility = View.VISIBLE
        }, 10000)

        scanDevice.visibility = View.VISIBLE
        scanProgress.visibility = View.VISIBLE
    }
}
