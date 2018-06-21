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
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_device.*
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.device.contract.IDeviceView
import org.fossasia.susi.ai.device.contract.IDevicePresenter
import timber.log.Timber


class DeviceActivity : AppCompatActivity(), IDeviceView {

    lateinit var devicePresenter: IDevicePresenter
    lateinit var mainWifi: WifiManager
    lateinit var receiverWifi: WifiReceiver
    var filter: IntentFilter? = null
    var broadCastRegistered = false
    private val PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out)
        setContentView(R.layout.activity_device)
        mainWifi = application.getSystemService(Context.WIFI_SERVICE) as WifiManager
        receiverWifi = WifiReceiver()
        devicePresenter = DevicePresenter(this)
        devicePresenter.onAttach(this)

        devicePresenter.searchDevices()
    }

    override fun askForPermissions() {
        noDeviceFound.visibility = View.GONE
        deviceTutorial.visibility = View.GONE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(Array<String>(1, { Manifest.permission.ACCESS_COARSE_LOCATION }),
                    PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
        } else {
            devicePresenter.isPermissionGranted(true)
            Timber.d("ASK PERMISSIONS ELSE")
        }
    }

    override fun showLocationIntentDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setView(R.layout.select_dialog_item_material)

        dialogBuilder.setTitle(R.string.location_access)
        dialogBuilder.setMessage(R.string.location_access_message)
        dialogBuilder.setPositiveButton("NEXT", { dialog, whichButton ->

            val callGPSSettingIntent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(callGPSSettingIntent, 0)

        })
        dialogBuilder.setNegativeButton("Cancel", { dialog, whichButton ->
        })
        val b = dialogBuilder.create()
        b.show()
    }

    override fun startScan() {
        mainWifi = application.getSystemService(Context.WIFI_SERVICE) as WifiManager
        filter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        receiverWifi = WifiReceiver()
        registerReceiver(receiverWifi, filter)
        broadCastRegistered = true
        mainWifi.startScan()
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
        Timber.d("Connected Successfully")
        scanDevice.visibility = View.GONE
        scanProgress.visibility = View.GONE
    }

    override fun showProgress() {
        scanProgress.getIndeterminateDrawable().setColorFilter(Color.parseColor("#ffc100"), android.graphics.PorterDuff.Mode.MULTIPLY)
        scanDevice.visibility = View.VISIBLE
        scanProgress.visibility = View.VISIBLE
    }

    override fun onDeviceConnectionError(title: String?, content: String?) {
        scanDevice.visibility = View.GONE
        scanProgress.visibility = View.GONE
        noDeviceFound.text = title
        deviceTutorial.text = content
        noDeviceFound.visibility = View.VISIBLE
        deviceTutorial.visibility = View.VISIBLE
    }

    fun chooseWifi(view: View) {
        devicePresenter.searchDevices()
    }

    override fun onPause() {
        if (broadCastRegistered)
            unregisterReceiver(receiverWifi)
        super.onPause()
    }

    override fun onResume() {
        if (filter != null) {
            registerReceiver(receiverWifi, filter)
            broadCastRegistered = true
        }
        super.onResume()
    }

    inner class WifiReceiver : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            Timber.d("Inside the app")
            var wifiList: List<ScanResult> = ArrayList<ScanResult>()
            wifiList = mainWifi.getScanResults()
            devicePresenter.inflateList(wifiList)

        }
    }

    override fun stopProgress() {
        scanDevice.visibility = View.GONE
        scanProgress.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 0) {
            devicePresenter.checkLocationEnabled()
            Timber.d("Onactivityresult")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION -> run {
                for (i in permissions.indices) {
                    when (permissions[i]) {
                        Manifest.permission.ACCESS_COARSE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            devicePresenter.isPermissionGranted(true)
                        }
                    }
                }
            }
        }
    }
}
