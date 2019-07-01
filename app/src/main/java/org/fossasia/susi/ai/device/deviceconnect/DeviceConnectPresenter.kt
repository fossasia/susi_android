package org.fossasia.susi.ai.device.deviceconnect

import android.content.Context
import android.location.LocationManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.AsyncTask
import io.realm.Realm
import org.fossasia.susi.ai.MainApplication
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.data.device.DeviceModel
import org.fossasia.susi.ai.data.UtilModel
import org.fossasia.susi.ai.data.contract.IDeviceModel
import org.fossasia.susi.ai.data.device.RoomModel
import org.fossasia.susi.ai.data.device.SpeakerAuth
import org.fossasia.susi.ai.data.device.SpeakerConfiguration
import org.fossasia.susi.ai.data.model.RoomsAvailable
import org.fossasia.susi.ai.dataclasses.AddDeviceQuery
import org.fossasia.susi.ai.device.deviceconnect.contract.IDeviceConnectPresenter
import org.fossasia.susi.ai.device.deviceconnect.contract.IDeviceConnectView
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.LocationHelper
import org.fossasia.susi.ai.helper.PrefManager
import timber.log.Timber

class DeviceConnectPresenter(context: Context, manager: WifiManager) : IDeviceConnectPresenter, IDeviceModel.onSendWifiCredentialsListener,
        IDeviceModel.onSetConfigurationListener, IDeviceModel.onSendAuthCredentialsListener {

    private var wifiManager = manager
    private var deviceConnectView: IDeviceConnectView? = null
    private var checkPermissions = false
    private var deviceModel: IDeviceModel = DeviceModel()
    private var isLocationOn = false
    private var SSID: String? = null
    private var isWifiEnabled = false
    lateinit var connections: ArrayList<String>
    private var utilModel: UtilModel = UtilModel(context)
    private lateinit var realm: Realm
    private val roomModel: RoomModel = RoomModel()

    override fun onAttach(deviceConnectView: IDeviceConnectView) {
        this.deviceConnectView = deviceConnectView
    }

    override fun searchDevices() {
        deviceConnectView?.askForPermissions()
        Timber.d(checkPermissions.toString() + "Check")
        if (checkPermissions) {
            checkLocationEnabled()
            checkWifiEnabled()
            if (isLocationOn && isWifiEnabled) {
                Timber.d("Location ON, WI-FI ON")
                deviceConnectView?.showProgress(utilModel.getString(R.string.scan_devices))

                deviceConnectView?.startScan(true)
            } else {
                if (!isWifiEnabled)
                    deviceConnectView?.showWifiIntentDialog()
                if (!isLocationOn)
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
        if (!list.isEmpty()) {
            deviceConnectView?.setupWiFiAdapter(connections)
            // deviceConnectView?.unregister()
        } else {
            deviceConnectView?.onDeviceConnectionError(utilModel.getString(R.string.no_device_found), utilModel.getString(R.string.setup_tut))
            // deviceConnectView?.unregister()
        }
    }

    override fun addRoom(room: String) {
        realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        var results = realm.where(RoomsAvailable::class.java).findAll()
        var id = results.size
        id++
        Timber.d("Added room")

        var addedRoomModel = realm.createObject(RoomsAvailable::class.java)
        addedRoomModel.id = id.toLong()
        addedRoomModel.room = room
        realm.commitTransaction()
        deviceConnectView?.showRooms()
    }

    override fun deleteRoom(room: String?) {
        realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val result = realm.where(RoomsAvailable::class.java).equalTo("room", room).findFirst()
        result?.deleteFromRealm()
        realm.commitTransaction()
        deviceConnectView?.showRooms()
    }

    override fun addDevice(queryObject: AddDeviceQuery) {
        roomModel.addDeviceToServer(queryObject)
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

    override fun checkWifiEnabled() {
        isWifiEnabled = wifiManager.isWifiEnabled
    }

    override fun checkLocationEnabled() {
        val locationManager = MainApplication.instance.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        isLocationOn = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override fun connectToDevice(networkSSID: String) {
        Timber.d("connectToWiFi() called with: ssid = [$networkSSID], key = password")
        SSID = networkSSID
        deviceConnectView?.showProgress(utilModel.getString(R.string.connecting_device))
        ConnectWifi().execute()
    }

    override fun disconnectConnectedWifi() {
        val wm = wifiManager
        val networkID = wm.connectionInfo.networkId
        wm.disconnect()
        wm.removeNetwork(networkID)
        wm.disableNetwork(networkID)
    }

    inner class ConnectWifi : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg p0: Void?): Void? {
            disconnectConnectedWifi()
            val wifiConfiguration = WifiConfiguration()
            wifiConfiguration.SSID = "\"" + SSID + "\""
            wifiConfiguration.preSharedKey = "\"" + "password" + "\""

            val networkId = wifiManager.addNetwork(wifiConfiguration)
            if (networkId != -1) {
                wifiManager.enableNetwork(networkId, true)
                // Use this to permanently save this network
                // Otherwise, it will disappear after a reboot
                wifiManager.saveConfiguration()
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            Timber.d("Connected")
        }
    }

    override fun makeConnectionRequest() {
        Timber.d("make request")
        //  deviceConnectView?.unregister()
        //  test data only
        //  deviceModel.sendAuthCredentials("y", "mohitkumar2k15@dtu.ac.in", "batbrain", this@DevicePresenter)
        //  deviceModel.sendWifiCredentials("Neelam", "9560247000", this@DevicePresenter)
        // deviceModel.setConfiguration("google", "google", "y", "n", this@DeviceConnectPresenter)
        searchWiFi()
    }

    override fun onSendCredentialSuccess() {
        Timber.d("WIFI - SUCCESSFUL")
        deviceConnectView?.onDeviceConnectionSuccess(utilModel.getString(R.string.wifi_success))
        deviceConnectView?.showPopUpDialog()
    }

    override fun onSendCredentialFailure(localMessage: String) {
        Timber.d("WIFI - FAILURE")
        deviceConnectView?.stopProgress()
        deviceConnectView?.onDeviceConnectionError(localMessage, utilModel.getString(R.string.wifi_error))
    }

    override fun onSendAuthSuccess() {
        Timber.d("AUTH - SUCCESSFUL")
        deviceConnectView?.stopProgress()
        deviceConnectView?.onDeviceConnectionSuccess(utilModel.getString(R.string.auth_success))
        makeConfigRequest()
    }

    override fun onSendAuthFailure(localMessage: String) {
        Timber.d("AUTH - FAILURE")
        deviceConnectView?.stopProgress()
        deviceConnectView?.onDeviceConnectionError(localMessage, utilModel.getString(R.string.auth_error))
    }

    override fun onSetConfigSuccess() {
        Timber.d("CONFIG - SUCCESS")
        deviceConnectView?.stopProgress()
        deviceConnectView?.onDeviceConnectionSuccess(utilModel.getString(R.string.connect_success))
        getLocation()
    }

    override fun getLocation() {
        val locationHelper: LocationHelper = LocationHelper(MainApplication.instance.applicationContext)
        locationHelper.getLocation()
        if (locationHelper.canGetLocation()) {
            deviceConnectView?.addDevice(locationHelper.latitude.toString(), locationHelper.longitude.toString())
            Timber.d("Got location. Latitude - " + locationHelper.toString())
        } else {
            deviceConnectView?.addDevice("12.1", "23.2")
            Timber.d("Failed to get Location")
        }
    }

    override fun onSetConfigFailure(localMessage: String) {
        Timber.d("CONFIG - FAILURE")
        deviceConnectView?.stopProgress()
        deviceConnectView?.onDeviceConnectionError(localMessage, utilModel.getString(R.string.config_error))
    }

    override fun searchWiFi() {
        deviceConnectView?.startScan(false)
    }

    override fun makeWifiRequest(ssid: String, password: String) {
        Timber.d("In here : WIFI REQUEST")
        deviceConnectView?.showProgress(utilModel.getString(R.string.connecting_device))
        deviceModel.sendWifiCredentials(ssid, password, this@DeviceConnectPresenter)
    }

    override fun makeConfigRequest() {
        Timber.d("In here : CONFIG REQUEST")
        deviceConnectView?.showProgress(utilModel.getString(R.string.connecting_device))
        deviceModel.setConfiguration(SpeakerConfiguration("google", "google", "y", "n"), this@DeviceConnectPresenter)
    }

    override fun makeAuthRequest(password: String) {
        Timber.d("In here : AUTH REQUEST")
        deviceConnectView?.showProgress(utilModel.getString(R.string.connecting_device))
        val email = PrefManager.getStringSet(Constant.SAVED_EMAIL)?.iterator()?.next()
        email?.let { SpeakerAuth("y", it, password) }?.let { deviceModel.sendAuthCredentials(it, this@DeviceConnectPresenter) }
    }
}