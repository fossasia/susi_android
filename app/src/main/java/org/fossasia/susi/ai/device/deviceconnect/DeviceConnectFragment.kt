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
import android.widget.EditText
import kotlinx.android.synthetic.main.fragment_device_connect.noDeviceFound
import kotlinx.android.synthetic.main.fragment_device_connect.deviceTutorial
import kotlinx.android.synthetic.main.fragment_device_connect.addDeviceButton
import kotlinx.android.synthetic.main.fragment_device_connect.scanDevice
import kotlinx.android.synthetic.main.fragment_device_connect.scanProgress
import kotlinx.android.synthetic.main.fragment_device_connect.wifiList
import kotlinx.android.synthetic.main.fragment_device_connect.scanHelp
import kotlinx.android.synthetic.main.fragment_device_connect.deviceList
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.data.UtilModel
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
    private val PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1
    private val VIEW_AVAILABLE_DEVICES = 1
    private val VIEW_AVAILABLE_WIFI = 0
    private var checkDevice: Boolean = false
    private val REQUEST_LOCATION_ACCESS = 101
    private val REQUEST_WIFI_ACCESS = 102

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_device_connect, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainWifi = context?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        deviceConnectPresenter = DeviceConnectPresenter(requireContext(), mainWifi)
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
        context?.registerReceiver(receiverWifi, filter)
    }

    override fun askForPermissions() {
        noDeviceFound.visibility = View.GONE
        deviceTutorial.visibility = View.GONE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (activity as DeviceActivity).checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(Array<String>(1, { Manifest.permission.ACCESS_COARSE_LOCATION }),
                    PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION)
        } else {
            deviceConnectPresenter.isPermissionGranted(true)
            Timber.d("ASK PERMISSIONS ELSE")
        }
    }

    override fun showLocationIntentDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setView(R.layout.select_dialog_item_material)

        dialogBuilder.setTitle(R.string.location_access)
        dialogBuilder.setMessage(R.string.location_access_message)
        dialogBuilder.setPositiveButton(R.string.next) { _, _ ->
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(callGPSSettingIntent, REQUEST_LOCATION_ACCESS)
        }
        dialogBuilder.setNegativeButton(R.string.cancel) { dialog, whichButton ->
            dialog.dismiss()
        }?.show()
    }

    /**
     * Shows dialog to turn on Wi-Fi
     */
    override fun showWifiIntentDialog() {
        var dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setView(R.layout.select_dialog_item_material)

        dialogBuilder.setTitle(R.string.wifi_access)
        dialogBuilder.setMessage(R.string.wifi_access_message)
        dialogBuilder.setPositiveButton(R.string.next) { dialog, whichButton ->
            val wifiIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
            startActivityForResult(wifiIntent, REQUEST_WIFI_ACCESS)
        }
        dialogBuilder.setNegativeButton(R.string.cancel) { dialog, whichButton ->
            dialog.dismiss()
        }

        dialogBuilder.create().show()
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
        scanHelp.visibility = View.GONE
        deviceList.visibility = View.VISIBLE
        deviceList.layoutManager = LinearLayoutManager(context)
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
        scanHelp.visibility = View.GONE
    }

    override fun onDeviceConnectionError(title: String?, content: String?) {
        unregister()
        context?.registerReceiver(receiverWifi, IntentFilter())
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
        if (requestCode == REQUEST_LOCATION_ACCESS || requestCode == REQUEST_WIFI_ACCESS) {
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
        onPause()
    }

    override fun onPause() {
        context?.unregisterReceiver(receiverWifi)
        super.onPause()
    }

    override fun onDeviceConnectionSuccess(message: String) {
        unregister()
        context?.registerReceiver(receiverWifi, IntentFilter())
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
                        val wifiInfo = mainWifi.connectionInfo
                        if (wifiInfo != null) {
                            val ssid = wifiInfo.ssid
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
        scanHelp.visibility = View.VISIBLE
        deviceList.visibility = View.GONE
        addDeviceButton.visibility = View.GONE
        wifiList.visibility = View.VISIBLE
        wifiList.layoutManager = LinearLayoutManager(context)
        wifiList.setHasFixedSize(true)
        recyclerAdapter = DevicesAdapter(scanList, deviceConnectPresenter, VIEW_AVAILABLE_WIFI)
        wifiList.adapter = recyclerAdapter
    }

    override fun showPopUpDialog() {
        val utilModel = UtilModel(activity as DeviceActivity)
        val view = LayoutInflater.from(activity).inflate(R.layout.get_password, null)
        val alertDialog = AlertDialog.Builder(activity as DeviceActivity).create()
        alertDialog.setTitle(utilModel.getString(R.string.thanks_wifi))
        alertDialog.setMessage(utilModel.getString(R.string.enter_password_mail))
        alertDialog.setCancelable(false)

        val password = view.findViewById<EditText>(R.id.edt_pass)

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, utilModel.getString(R.string.next)) { dialog, which ->
            deviceConnectPresenter.makeAuthRequest(password.text.toString())
        }

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, utilModel.getString(R.string.cancel)) { dialog, which ->
            alertDialog.dismiss()
        }

        alertDialog.setView(view)
        alertDialog.show()
    }
}
