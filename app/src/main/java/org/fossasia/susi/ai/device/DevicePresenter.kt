package org.fossasia.susi.ai.device

import android.content.Context
import org.fossasia.susi.ai.device.contract.IDevicePresenter
import org.fossasia.susi.ai.device.contract.IDeviceView
import android.location.LocationManager
import android.net.wifi.ScanResult
import org.fossasia.susi.ai.MainApplication
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.data.UtilModel
import timber.log.Timber


class DevicePresenter(deviceActivity: DeviceActivity) : IDevicePresenter {

    private var deviceView: IDeviceView? = null
    var check = false
    var isLocationOn = false
    val connections = ArrayList<String>()
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
                deviceView?.showProgress()
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
        for (i in list.indices) {
            connections.add(list[i].SSID)
            Timber.d(connections.get(i))
        }

        if (connections.size > 0) {
            deviceView?.onDeviceConnectedSuccess()
        } else {
            deviceView?.onDeviceConnectionError(utilModel.getString(R.string.no_device_found), utilModel.getString(R.string.setup_tut))
        }
    }

    override fun isPermissionGranted(b: Boolean) {
        check = b
    }

    override fun checkLocationEnabled() {
        val locationManager = MainApplication.getInstance().applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            isLocationOn = true
            deviceView?.startScan()
        } else {
            isLocationOn = false
        }
    }

}
