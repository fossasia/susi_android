package org.fossasia.susi.ai.device


import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_device.*
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.device.contract.IDeviceView
import timber.log.Timber


class DeviceActivity : AppCompatActivity(), IDeviceView {

    lateinit var devicePresenter: DevicePresenter
    lateinit var mainWifi: WifiManager
    lateinit var receiverWifi: WifiReceiver
    lateinit var filter: IntentFilter
    private val PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out)
        setContentView(R.layout.activity_device)

        filter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        showProgress()
        devicePresenter = DevicePresenter(this)
        devicePresenter.onAttach(this)
        startScan()
    }

    private fun startScan() {
        noDeviceFound.visibility = View.GONE
        deviceTutorial.visibility = View.GONE
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(Array<String>(1,{Manifest.permission.ACCESS_COARSE_LOCATION}),
                    PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }else{
            mainWifi = application.getSystemService(Context.WIFI_SERVICE) as WifiManager

            receiverWifi = WifiReceiver()
            registerReceiver(receiverWifi,filter)
            mainWifi.startScan()
            //do something, permission was previously granted; or legacy device
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
        scanDevice.visibility = View.VISIBLE
        scanProgress.visibility = View.VISIBLE
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

    override fun onPause() {
        unregisterReceiver(receiverWifi)
        super.onPause()
    }

    override fun onResume() {
        registerReceiver(receiverWifi, filter)
        super.onResume()
    }

    inner class WifiReceiver: BroadcastReceiver() {

        override fun onReceive(p0: Context?, p1: Intent?) {


                Toast.makeText(p0, "Inside the app", Toast.LENGTH_LONG).show()

                val connections = ArrayList<String>()

                var sb = StringBuilder()
                var wifiList: List<ScanResult> = ArrayList<ScanResult>()
                wifiList = mainWifi.getScanResults()
                Timber.d("size " + wifiList.size)
                for (i in wifiList.indices) {
                    connections.add(wifiList[i].SSID)
                    Timber.d(connections.get(i))
                }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION -> run {
                for (i in permissions.indices) {
                    when (permissions[i]) {
                        Manifest.permission.ACCESS_COARSE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                          //  chatPresenter.getLocationFromLocationService()
                            startScan()
                        }
                    }
                }
            }
        }
    }
}
