package org.fossasia.susi.ai.device


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_device.*
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.device.contract.IDeviceView
import org.fossasia.susi.ai.device.DeviceActivity.WifiReceiver




class DeviceActivity : AppCompatActivity(), IDeviceView {

    lateinit var devicePresenter: DevicePresenter
    lateinit var mainWifi: WifiManager
    lateinit var receiverWifi: WifiReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out)
        setContentView(R.layout.activity_device)


        showProgress()
        mainWifi = getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager

        receiverWifi = WifiReceiver();
        registerReceiver(receiverWifi, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        if(mainWifi.isWifiEnabled == false) {
            mainWifi.setWifiEnabled(true);
        }
        mainWifi.startScan()

        devicePresenter = DevicePresenter(this)
        devicePresenter.onAttach(this)

        //startScan()
    }

    private fun startScan() {
        noDeviceFound.visibility = View.GONE
        deviceTutorial.visibility = View.GONE

        Handler().postDelayed({
            mainWifi = application.getSystemService(Context.WIFI_SERVICE) as WifiManager

            receiverWifi = WifiReceiver()
            registerReceiver(receiverWifi, IntentFilter(
                    WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
            mainWifi.startScan()
            startScan()
        }, 1000)

        scanDevice.visibility = View.VISIBLE
        scanProgress.visibility = View.VISIBLE
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
        scanProgress.getIndeterminateDrawable().setColorFilter(Color.parseColor("#ffc100"), android.graphics.PorterDuff.Mode.MULTIPLY)
    }

    override fun onDeviceConnectionError() {

        Handler().postDelayed({
            scanDevice.visibility = View.GONE
            scanProgress.visibility = View.GONE
            noDeviceFound.visibility = View.VISIBLE
            deviceTutorial.visibility = View.VISIBLE
        }, 10000)


    }

    fun chooseWifi(view: View) {
        startScan()
    }

    inner class WifiReceiver: BroadcastReceiver() {

        override fun onReceive(p0: Context?, p1: Intent?) {

            val connections = ArrayList<String>()

            var sb = StringBuilder()
            var wifiList: List<ScanResult> = ArrayList<ScanResult>()
            wifiList = mainWifi.getScanResults()
            for (i in wifiList.indices) {
                connections.add(wifiList[i].SSID)
                Toast.makeText(p0,connections.get(i),Toast.LENGTH_LONG).show()
            }
        }

    }
}
