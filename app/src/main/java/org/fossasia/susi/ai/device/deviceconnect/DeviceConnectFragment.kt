package org.fossasia.susi.ai.device.deviceconnect


import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.wifi.SupplicantState
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_device_connect.*

import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.device.DeviceActivity
import org.fossasia.susi.ai.device.deviceconnect.adapters.recycleradapters.DevicesAdapter
import org.fossasia.susi.ai.device.deviceconnect.contract.IDeviceConnectPresenter
import org.fossasia.susi.ai.device.deviceconnect.contract.IDeviceConnectView
import timber.log.Timber

/**
 * @author batbrain7
 * Created on 11/07/18
 * Fragment that displays the UI to connect to the device
 */
class DeviceConnectFragment : Fragment(), IDeviceConnectView {

    lateinit var deviceConnectPresenter: IDeviceConnectPresenter
    lateinit var mainWifi: WifiManager
    lateinit var receiverWifi: WifiReceiver
    lateinit var recyclerAdapter: DevicesAdapter
    private var filter: IntentFilter? = null
    private var b = false
    private val PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;
    private val VIEW_AVAILABLE_DEVICES = 1;
    private val VIEW_AVAILABLE_WIFI = 0;
    private var checkDevice: Boolean = false;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_device_connect, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainWifi = (activity as DeviceActivity).applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        deviceConnectPresenter = DeviceConnectPresenter(activity as DeviceActivity, mainWifi)
        deviceConnectPresenter.onAttach(this)
        receiverWifi = WifiReceiver()
        deviceConnectPresenter.searchDevices()

        addDeviceButton.setOnClickListener {
            deviceConnectPresenter.searchDevices()
        }
    }

    override fun onResume() {
        super.onResume()
        filter = IntentFilter()
        filter?.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        filter?.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)
        receiverWifi = WifiReceiver()
        (activity as DeviceActivity).registerReceiver(receiverWifi, filter)
        b = true
    }

    override fun askForPermissions() {
        noDeviceFound.visibility = View.GONE
        deviceTutorial.visibility = View.GONE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (activity as DeviceActivity).checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(Array<String>(1, { Manifest.permission.ACCESS_COARSE_LOCATION }),
                    PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
        } else {
            deviceConnectPresenter.isPermissionGranted(true)
            Timber.d("ASK PERMISSIONS ELSE")
        }
    }

    override fun showLocationIntentDialog() {
        val dialogBuilder = AlertDialog.Builder(activity as DeviceActivity)
        dialogBuilder.setView(R.layout.select_dialog_item_material)

        dialogBuilder.setTitle(R.string.location_access)
        dialogBuilder.setMessage(R.string.location_access_message)
        dialogBuilder.setPositiveButton("NEXT", { dialog, whichButton ->

            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(callGPSSettingIntent, 0)

        })
        dialogBuilder.setNegativeButton("Cancel", { dialog, whichButton ->
        })
        val b = dialogBuilder.create()
        b.show()
    }

    override fun startScan(isDevice: Boolean) {
        checkDevice = isDevice
        Timber.d(isDevice.toString())
        mainWifi.startScan()
    }

    override fun setupDeviceAdapter(scanList: List<String>) {
        Timber.d("Connected Successfully")
        scanDevice.visibility = View.GONE
        scanProgress.visibility = View.GONE
        wifiList.visibility = View.GONE
        deviceList.visibility = View.VISIBLE
        deviceList.layoutManager = LinearLayoutManager(activity as DeviceActivity)
        deviceList.setHasFixedSize(true)
        recyclerAdapter = DevicesAdapter(scanList, deviceConnectPresenter, VIEW_AVAILABLE_DEVICES)
        deviceList.adapter = recyclerAdapter

    }

    override fun showProgress(title: String?) {
        scanProgress.getIndeterminateDrawable().setColorFilter(Color.parseColor("#ffc100"), android.graphics.PorterDuff.Mode.MULTIPLY)
        scanDevice.text = title
        scanDevice.visibility = View.VISIBLE
        scanProgress.visibility = View.VISIBLE
        deviceList.visibility = View.GONE
        wifiList.visibility = View.GONE
    }

    override fun onDeviceConnectionError(title: String?, content: String?) {
        unregister()
        (activity as DeviceActivity).registerReceiver(receiverWifi,IntentFilter())
        scanDevice.visibility = View.GONE
        scanProgress.visibility = View.GONE
        noDeviceFound.text = title
        deviceList.visibility = View.GONE
        deviceTutorial.text = content
        wifiList.visibility = View.GONE
        noDeviceFound.visibility = View.VISIBLE
        deviceTutorial.visibility = View.VISIBLE
    }

    override fun stopProgress() {
        scanDevice.visibility = View.GONE
        scanProgress.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 0) {
            deviceConnectPresenter.searchDevices()

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
                            deviceConnectPresenter.isPermissionGranted(true)
                        }
                    }
                }
            }
        }
    }

    override fun unregister() {
        b = false
        onPause()
    }

    override fun onPause() {
        (activity as DeviceActivity).unregisterReceiver(receiverWifi)
        super.onPause()
    }

    override fun onDeviceConnectionSuccess(message: String) {
        unregister()
        (activity as DeviceActivity).registerReceiver(receiverWifi,IntentFilter())
        scanProgress.visibility = View.GONE
        scanDevice.visibility = View.VISIBLE
        wifiList.visibility = View.GONE
        deviceList.visibility = View.GONE
        scanDevice.setText(message)
    }

    inner class WifiReceiver : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            Timber.d("Inside the app")
            if (p1 != null) {
                if (p1.action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                    val wifiList = mainWifi.getScanResults()
                    Timber.d("Check %s", checkDevice)
                    if (checkDevice)
                        deviceConnectPresenter.availableDevices(wifiList)
                    else
                        deviceConnectPresenter.availableWifi(wifiList)
                } else if (p1.action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
                    Timber.d("Wifi changes")
                    if (p1.getParcelableExtra<Parcelable>(WifiManager.EXTRA_NEW_STATE) == SupplicantState.COMPLETED) {
                        Timber.d("Wifi Changes #2")
                        val wi = mainWifi.connectionInfo
                        if (wi != null) {
                            val ssid = wi.ssid
                            Timber.d(ssid)
                            if (ssid.equals("\"SUSI.AI\"")) {
                                Timber.d("Going to make connection")
                                deviceConnectPresenter.makeConnectionRequest()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun setupWiFiAdapter(scanList: ArrayList<String>) {
        Timber.d("Setup Wifi adapter")
        scanDevice.setText(R.string.choose_wifi)
        scanList.remove("SUSI.AI")
        scanProgress.visibility = View.GONE
        deviceList.visibility = View.GONE
        addDeviceButton.visibility = View.GONE
        wifiList.visibility = View.VISIBLE
        wifiList.layoutManager = LinearLayoutManager(activity as DeviceActivity)
        wifiList.setHasFixedSize(true)
        recyclerAdapter = DevicesAdapter(scanList, deviceConnectPresenter, VIEW_AVAILABLE_WIFI)
        wifiList.adapter = recyclerAdapter
    }

}
