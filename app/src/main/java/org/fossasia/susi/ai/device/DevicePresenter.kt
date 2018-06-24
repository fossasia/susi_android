package org.fossasia.susi.ai.device

import android.content.Context
import org.fossasia.susi.ai.device.contract.IDevicePresenter
import org.fossasia.susi.ai.device.contract.IDeviceView
import android.location.LocationManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import org.fossasia.susi.ai.MainApplication
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.data.UtilModel
import timber.log.Timber
import android.net.NetworkInfo
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.AsyncTask

/*
*   Created by batbrain7 on 22/06/18
*   This is the presenter for the device activity where the logic of connecting to the device is written.
 */


class DevicePresenter(deviceActivity: DeviceActivity, manager: WifiManager) : IDevicePresenter {

    var mWifiManager = manager
    private var deviceView: IDeviceView? = null
    var check = false
    var isLocationOn = false
    var SSID: String? = null
    lateinit var connections: ArrayList<String>
    private var utilModel: UtilModel = UtilModel(deviceActivity)

    override fun onAttach(deviceView: IDeviceView) {
        this.deviceView = deviceView
    }

    override fun searchDevices() {
        deviceView?.askForPermissions()
        Timber.d(check.toString() + "Check")
        if (check) {
            checkLocationEnabled()
            if (isLocationOn) {
                Timber.d("Location ON")
                deviceView?.showProgress(utilModel.getString(R.string.scan_devices))
                deviceView?.startScan()
            } else {
                deviceView?.showLocationIntentDialog()
            }
        } else {
            deviceView?.askForPermissions()
        }
    }

    override fun onDetach() {
        this.deviceView = null
    }

    override fun getAvailableDevices() {

    }

    override fun inflateList(list: List<ScanResult>) {
        Timber.d("size " + list.size)
        connections = ArrayList<String>()
        for (i in list.indices) {
            if (list[i].SSID.equals(utilModel.getString(R.string.device_name)))
                connections.add(list[i].SSID)
        }

        if (connections.size > 0) {
            deviceView?.setupAdapter(connections)
            deviceView?.unregister()
        } else {
            deviceView?.onDeviceConnectionError(utilModel.getString(R.string.no_device_found), utilModel.getString(R.string.setup_tut))
            deviceView?.unregister()
        }
    }

    override fun isPermissionGranted(b: Boolean) {
        check = b
    }

    override fun checkLocationEnabled() {
        val locationManager = MainApplication.getInstance().applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            isLocationOn = true
        } else {
            isLocationOn = false
        }
    }

    override fun connectToDevice(networkSSID: String) {
        Timber.d("connectToWiFi() called with: ssid = [$networkSSID], key = password")
        SSID = networkSSID
        ConnectWifi().execute()
    }

    inner class ConnectWifi: AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg p0: Void?): Void? {
            val wifiConfiguration = WifiConfiguration()
            wifiConfiguration.SSID = "\"" + SSID + "\""
            wifiConfiguration.preSharedKey = "\"" + "password" + "\""

            val networkId = mWifiManager.addNetwork(wifiConfiguration)
            if (networkId != -1) {
                mWifiManager.enableNetwork(networkId, true)
                // Use this to permanently save this network
                // Otherwise, it will disappear after a reboot
                mWifiManager.saveConfiguration()
            }
            return null;
        }

        override fun onProgressUpdate(vararg values: Void?) {
            deviceView?.showProgress(utilModel.getString(R.string.connecting_device))
        }

        override fun onPostExecute(result: Void?) {
            deviceView?.onDeviceConnectionSuccess()
        }
    }
}
