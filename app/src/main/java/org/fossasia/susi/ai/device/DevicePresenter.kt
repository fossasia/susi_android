package org.fossasia.susi.ai.device

import android.content.Context
import org.fossasia.susi.ai.device.contract.IDevicePresenter
import org.fossasia.susi.ai.device.contract.IDeviceView
import android.location.LocationManager
import android.net.wifi.ScanResult
import org.fossasia.susi.ai.MainApplication
import org.fossasia.susi.ai.R
import timber.log.Timber


class DevicePresenter(deviceActivity: DeviceActivity) : IDevicePresenter {

    private var deviceView: IDeviceView? = null
    var check = false
    var isLocationOn = false
    val connections = ArrayList<String>()

    override fun onAttach(deviceView: IDeviceView) {
        this.deviceView = deviceView
    }

    override fun searchDevices() {
        deviceView?.askForPermissions()
        if (check) {
            deviceView?.showLocationIntentDialog()
            if (isLocationOn) {
                deviceView?.startScan()

                if (connections.size > 0) {
                    deviceView?.onDeviceConnectedSuccess()
                } else {
                    deviceView?.onDeviceConnectionError(R.string.no_device_found,R.string.setup_tut)
                }
            } else {

            }

        } else {
            // please enable permissions
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

}
