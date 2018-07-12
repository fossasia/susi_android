package org.fossasia.susi.ai.device.deviceconnect

import android.content.Context
import android.location.LocationManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.AsyncTask
import org.fossasia.susi.ai.MainApplication
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.data.DeviceModel
import org.fossasia.susi.ai.data.UtilModel
import org.fossasia.susi.ai.data.contract.IDeviceModel
import org.fossasia.susi.ai.device.DeviceActivity
import org.fossasia.susi.ai.device.deviceconnect.contract.IDeviceConnectPresenter
import org.fossasia.susi.ai.device.deviceconnect.contract.IDeviceConnectView
import timber.log.Timber

class DeviceConnectPresenter(deviceActivity: DeviceActivity, manager: WifiManager) : IDeviceConnectPresenter, IDeviceModel.onSendWifiCredentialsListener,
        IDeviceModel.onSetConfigurationListener, IDeviceModel.onSendAuthCredentialsListener {

    private var mWifiManager = manager
    private var deviceConnectView: IDeviceConnectView? = null
    private var checkPermissions = false
    private var deviceModel: IDeviceModel = DeviceModel()
    private var isLocationOn = false
    private var SSID: String? = null
    lateinit var connections: ArrayList<String>
    private var utilModel: UtilModel = UtilModel(deviceActivity)

    override fun onAttach(deviceConnectView: IDeviceConnectView) {
        this.deviceConnectView = deviceConnectView
    }

    override fun searchDevices() {
        deviceConnectView?.askForPermissions()
        Timber.d(checkPermissions.toString() + "Check")
        if (checkPermissions) {
            checkLocationEnabled()
            if (isLocationOn) {
                Timber.d("Location ON")
                deviceConnectView?.showProgress(utilModel.getString(R.string.scan_devices))
                deviceConnectView?.startScan(true)
            } else {
                deviceConnectView?.showLocationIntentDialog()
            }
        } else {
            deviceConnectView?.askForPermissions()
        }
    }

    override fun onDetach() {
        this.deviceConnectView = null
    }

    override fun availableWifi(list: List<ScanResult>) {
        connections = ArrayList<String>()
        for (i in list.indices) {
            connections.add(list[i].SSID)
        }

        deviceConnectView?.unregister()
        if (!list.isEmpty()) {
            deviceConnectView?.setupWiFiAdapter(connections)
        } else {
            deviceConnectView?.onDeviceConnectionError(utilModel.getString(R.string.no_device_found), utilModel.getString(R.string.setup_tut))
        }
    }

    override fun availableDevices(list: List<ScanResult>) {
        Timber.d("size " + list.size)
        connections = ArrayList<String>()
        for (i in list.indices) {
            if (list[i].SSID.equals(utilModel.getString(R.string.device_name)))
                connections.add(list[i].SSID)
        }

        if (!connections.isEmpty()) {
            deviceConnectView?.setupDeviceAdapter(connections)
        } else {
            deviceConnectView?.onDeviceConnectionError(utilModel.getString(R.string.no_device_found), utilModel.getString(R.string.setup_tut))
        }
    }

    override fun isPermissionGranted(b: Boolean) {
        checkPermissions = b
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
        deviceConnectView?.showProgress(utilModel.getString(R.string.connecting_device))
        ConnectWifi().execute()
    }

    inner class ConnectWifi : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg p0: Void?): Void? {
            val wifiConfiguration = WifiConfiguration()
            wifiConfiguration.SSID = "\"" + SSID + "\""
            wifiConfiguration.preSharedKey = "\"" + "password" + "\""

            val networkId = mWifiManager.addNetwork(wifiConfiguration)
//            if (networkId != -1) {
//                mWifiManager.enableNetwork(networkId, true)
//                // Use this to permanently save this network
//                // Otherwise, it will disappear after a reboot
//                mWifiManager.saveConfiguration()
//            }
            mWifiManager.disconnect();
            mWifiManager.enableNetwork(networkId, true);
            mWifiManager.reconnect();
            return null
        }

        override fun onPostExecute(result: Void?) {
            Timber.d("Connected")

        }
    }

    override fun makeConnectionRequest() {
        Timber.d("make request")
        // deviceConnectView?.unregister()
        // test data only
        //  deviceModel.sendAuthCredentials("y", "mohitkumar2k15@dtu.ac.in", "batbrain", this@DevicePresenter)
        //  deviceModel.sendWifiCredentials("Neelam", "9560247000", this@DevicePresenter)
        // deviceModel.setConfiguration("google", "google", "y", "n", this@DeviceConnectPresenter)
        searchWiFi()
    }

    override fun onSendCredentialSuccess() {
        Timber.d("WIFI - SUCCESSFUL")
        deviceConnectView?.onDeviceConnectionSuccess()
    }

    override fun onSendCredentialFailure() {
        Timber.d("WIFI - FAILURE")
     //   deviceConnectView?.onDeviceConnectionError("Wifi Cred Failure", "Not done properly")
    }

    override fun onSendAuthSuccess() {
        Timber.d("AUTH - SUCCESSFUL")
        deviceConnectView?.onDeviceConnectionSuccess()
    }

    override fun onSendAuthFailure() {
        Timber.d("AUTH - FAILURE")
        deviceConnectView?.onDeviceConnectionError("Auth Failure", "Not done properly")
    }

    override fun onSetConfigSuccess() {
        Timber.d("CONFIG - SUCCESS")
        deviceConnectView?.onDeviceConnectionSuccess()

    }

    override fun onSetConfigFailure() {
        Timber.d("CONFIG - FAILURE")
        deviceConnectView?.onDeviceConnectionError("Configuration Failure", "Not done properly")
    }

    override fun searchWiFi() {
        deviceConnectView?.startScan(false)
    }
}