package org.fossasia.susi.ai.device.deviceconnect

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.wifi.SupplicantState
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.realm.Realm
import kotlinx.android.synthetic.main.connect_susiai_speaker.cancel_susiai_connection
import kotlinx.android.synthetic.main.connect_susiai_speaker.cannot_connect_susiai
import kotlinx.android.synthetic.main.connect_susiai_speaker.connected_susiai
import kotlinx.android.synthetic.main.finish_setup_layout.success_message
import kotlinx.android.synthetic.main.finish_setup_layout.success_setup
import kotlinx.android.synthetic.main.fragment_device_connect.account_wizard
import kotlinx.android.synthetic.main.fragment_device_connect.addDeviceButton
import kotlinx.android.synthetic.main.fragment_device_connect.connect_wizard
import kotlinx.android.synthetic.main.fragment_device_connect.connection_susiai_main_screen
import kotlinx.android.synthetic.main.fragment_device_connect.deviceList
import kotlinx.android.synthetic.main.fragment_device_connect.deviceTutorial
import kotlinx.android.synthetic.main.fragment_device_connect.noDeviceFound
import kotlinx.android.synthetic.main.fragment_device_connect.password_layout
import kotlinx.android.synthetic.main.fragment_device_connect.room
import kotlinx.android.synthetic.main.fragment_device_connect.room_wizard
import kotlinx.android.synthetic.main.fragment_device_connect.scanDevice
import kotlinx.android.synthetic.main.fragment_device_connect.scanHelp
import kotlinx.android.synthetic.main.fragment_device_connect.scanProgress
import kotlinx.android.synthetic.main.fragment_device_connect.showWifi
import kotlinx.android.synthetic.main.fragment_device_connect.success_setup_screen
import kotlinx.android.synthetic.main.fragment_device_connect.wifiList
import kotlinx.android.synthetic.main.fragment_device_connect.wifi_password_layout
import kotlinx.android.synthetic.main.fragment_device_connect.wifi_wizard
import kotlinx.android.synthetic.main.password_layout.account_auth_password_input
import kotlinx.android.synthetic.main.password_layout.account_email
import kotlinx.android.synthetic.main.password_layout.anonymous_mode
import kotlinx.android.synthetic.main.password_layout.password_finish
import kotlinx.android.synthetic.main.password_layout.password_previous
import kotlinx.android.synthetic.main.room_layout.add_room
import kotlinx.android.synthetic.main.room_layout.edt_room
import kotlinx.android.synthetic.main.room_layout.room_next
import kotlinx.android.synthetic.main.room_layout.room_previous
import kotlinx.android.synthetic.main.wifi_password_layout.wifi_name_show
import kotlinx.android.synthetic.main.wifi_password_layout.wifi_next
import kotlinx.android.synthetic.main.wifi_password_layout.wifi_password_input
import kotlinx.android.synthetic.main.wifi_password_layout.wifi_previous
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.data.model.RoomsAvailable
import org.fossasia.susi.ai.device.DeviceActivity
import org.fossasia.susi.ai.device.DeviceActivity.Companion.ANONYMOUS_MODE
import org.fossasia.susi.ai.device.DeviceActivity.Companion.macId
import org.fossasia.susi.ai.device.deviceconnect.adapters.recycleradapters.DevicesAdapter
import org.fossasia.susi.ai.device.deviceconnect.adapters.recycleradapters.RoomsAdapter
import org.fossasia.susi.ai.device.deviceconnect.contract.IDeviceConnectPresenter
import org.fossasia.susi.ai.device.deviceconnect.contract.IDeviceConnectView
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.PrefManager
import org.fossasia.susi.ai.helper.Utils
import org.fossasia.susi.ai.skills.SkillsActivity
import org.fossasia.susi.ai.skills.SkillsActivity.Companion.REDIRECTED_FROM
import org.fossasia.susi.ai.skills.SkillsActivity.Companion.SETTINGS_FRAGMENT
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
    private lateinit var realm: Realm
    private val availableRoomsList: ArrayList<AvailableRoomsFormat> = ArrayList()
    private lateinit var availableRoomsRecyclerView: RecyclerView
    private var availableRoomsAdapter: RecyclerView.Adapter<*>? = null
    private var roomNameSelected: String? = null
    private lateinit var rootView: View
    private var showWifiList: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_device_connect, container, false)
        realm = Realm.getDefaultInstance()
        availableRoomsRecyclerView = rootView.findViewById(R.id.rooms_available)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainWifi = context?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        deviceConnectPresenter = DeviceConnectPresenter(requireContext(), mainWifi)
        deviceConnectPresenter.onAttach(this)

        connectionMainScreen() // 1st step
    }

    override fun onResume() {
        super.onResume()
        filter = IntentFilter()
        filter?.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        filter?.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)
        receiverWifi = WifiReceiver()
        context?.registerReceiver(receiverWifi, filter)
    }

    override fun connectionMainScreen() {
        connection_susiai_main_screen.visibility = View.VISIBLE
        showWifi.visibility = View.GONE
        scanHelp.visibility = View.GONE
        showWifiList = false
        addDeviceButton.visibility = View.GONE
        scanHelp.visibility = View.GONE
        deviceList.visibility = View.GONE
        wifi_password_layout.visibility = View.GONE
        wifiList.visibility = View.GONE
        connect_wizard.background = ContextCompat.getDrawable(requireContext(), R.drawable.border_blue)
        wifi_wizard.background = ContextCompat.getDrawable(requireContext(), R.drawable.border_normal)
        wifi_wizard.setTextColor(Color.BLACK)

        // If user fails to connect to the susiai hotspot
        cannot_connect_susiai.setOnClickListener {
            showDialog(getString(R.string.cannot_connect_details), getString(R.string.cannot_connect_button))
        }

        connected_susiai.setOnClickListener {
            // If user successfully connects to susiai hotspot
            if (deviceConnectPresenter.getSUSIAIConnectionInfo()) {
                wifiSetup()
            } else {
                // Not connected to susiai wifi
                showDialog(getString(R.string.not_connected_susiai_details), getString(R.string.wifi_connection_error))
            }
        }

        // If user wants to cancel the setup
        cancel_susiai_connection.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    // 2nd step of setting up the wifi
    override fun wifiSetup() {
        room.visibility = View.GONE
        showWifiList = true
        connection_susiai_main_screen.visibility = View.GONE
        wifi_password_layout.visibility = View.GONE
        showWifi.visibility = View.VISIBLE
        scanHelp.visibility = View.VISIBLE
        room_wizard.background = ContextCompat.getDrawable(requireContext(), R.drawable.border_normal)
        room_wizard.setTextColor(Color.BLACK)
        wifi_wizard.background = ContextCompat.getDrawable(requireContext(), R.drawable.border_blue)
        wifi_wizard.setTextColor(Color.WHITE)
        connection_susiai_main_screen.visibility = View.GONE
        showProgress(getString(R.string.scan_available_wifi))
        deviceConnectPresenter.searchWiFi()
    }

    override fun showDialog(message: String, title: String) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                }).setTitle(title).create().show()
    }

    // Prompt password for selected wifi
    override fun selectedWifi(wifiName: String) {
        addDeviceButton.visibility = View.GONE
        deviceList.visibility = View.GONE
        wifiList.visibility = View.GONE
        password_layout.visibility = View.GONE
        showWifiList = false
        connection_susiai_main_screen.visibility = View.GONE
        showWifi.visibility = View.GONE
        scanHelp.visibility = View.GONE
        wifi_password_layout.visibility = View.VISIBLE
        wifi_name_show.text = wifiName

        wifi_previous.setOnClickListener {
            connectionMainScreen()
        }

        wifi_next.setOnClickListener {
            deviceConnectPresenter.makeWifiRequest(wifiName, wifi_password_input.text.toString())
        }
    }

    // Function to show available rooms in the 3rd step
    override fun rooms() {
        stopProgress()
        room.visibility = View.VISIBLE
        addDeviceButton.visibility = View.GONE
        deviceList.visibility = View.GONE
        wifiList.visibility = View.GONE
        wifi_password_layout.visibility = View.GONE
        password_layout.visibility = View.GONE
        showWifiList = false
        connection_susiai_main_screen.visibility = View.GONE
        showWifi.visibility = View.GONE
        room_wizard.background = ContextCompat.getDrawable(requireContext(), R.drawable.border_blue)
        account_wizard.background = ContextCompat.getDrawable(requireContext(), R.drawable.border_normal)
        account_wizard.setTextColor(Color.BLACK)
        room_wizard.setTextColor(Color.WHITE)
        showRooms()

        if (roomNameSelected.isNullOrEmpty()) {
            room_next.setBackgroundColor(resources.getColor(R.color.default_bg))
            room_next.setTextColor(Color.BLACK)
            room_next.isClickable = false
        }

        add_room.setOnClickListener {
            val roomName = edt_room.text.toString()
            if (!roomName.isEmpty()) {
                deviceConnectPresenter.addRoom(roomName)
                edt_room.text.clear()
                Utils.hideSoftKeyboard(context, rootView)
            }
        }

        room_next.setOnClickListener {
            if (!roomNameSelected.isNullOrEmpty()) {
                // To call the next screen when room name is selected
                // Send the room name to the speaker by making another endpoint
                deviceConnectPresenter.makeAddRoomRequest(roomNameSelected.toString())
            }
        }

        room_previous.setOnClickListener {
            // To call the wifi setup page
            roomNameSelected = null
            wifiSetup()
        }
    }

    // Final step of connectivity
    override fun passwordLayoutSetup() {
        password_layout.visibility = View.VISIBLE
        account_email.text = PrefManager.getStringSet(Constant.SAVED_EMAIL)?.iterator()?.next()
        room.visibility = View.GONE
        room_wizard.background = ContextCompat.getDrawable(requireContext(), R.drawable.border_blue)
        account_wizard.background = ContextCompat.getDrawable(requireContext(), R.drawable.border_blue)
        account_wizard.setTextColor(Color.WHITE)

        // If anonymous mode selected move to finish send the config request directly
        anonymous_mode.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                showAnonymousDialog()
            }
        }

        // Set on Text Change listener in the password field
        val watch = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // To change body of created functions use File | Settings | File Templates.
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // To change body of created functions use File | Settings | File Templates.
            }

            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                // If password input is not empty then make the finish button enable else disable it and change the colour accordingly
                if (charSequence.trim().length > 0) {
                    password_finish.isEnabled = true
                    password_finish.isClickable = true
                    password_finish.background = ContextCompat.getDrawable(requireContext(), R.drawable.border_blue)
                    password_finish.setTextColor(resources.getColor(R.color.md_white_1000))
                } else {
                    password_finish.isEnabled = false
                    password_finish.isClickable = false
                    password_finish.background = ContextCompat.getDrawable(requireContext(), R.drawable.border_normal)
                    password_finish.setTextColor(resources.getColor(R.color.blue_grey_300))
                }
            }
        }
        account_auth_password_input.addTextChangedListener(watch)

        // Action of previous button
        password_previous.setOnClickListener {
            rooms()
        }

        // Action of finish button
        password_finish.setOnClickListener {
            if (password_finish.isEnabled && password_finish.isClickable) {
                // Send the password request
                finishSetup()
            }
        }
    }

    override fun finishSetup() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage(getString(R.string.finish_setup_details))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.finish_setup_button), DialogInterface.OnClickListener { dialog, id ->
                    deviceConnectPresenter.makeAuthRequest(account_auth_password_input.text.toString())
                    ANONYMOUS_MODE = false
                    dialog.cancel()
                })
                .setNegativeButton(getString(R.string.go_back), DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                }).setTitle(getString(R.string.finish_setup_title))
                .create()
                .show()
    }

    override fun successSetup() {
        password_layout.visibility = View.GONE
        success_setup_screen.visibility = View.VISIBLE
        if (ANONYMOUS_MODE) {
            success_message.text = getString(R.string.success_setup_anonymous)
        } else {
            success_message.text = getString(R.string.succesfully_setup)
        }
        unregister()
        context?.registerReceiver(receiverWifi, IntentFilter())

        success_setup.setOnClickListener {
            val intent = Intent(context, SkillsActivity::class.java)
            intent.putExtra(REDIRECTED_FROM, SETTINGS_FRAGMENT)
            startActivity(intent)
        }
    }

    override fun showRooms() {
        availableRoomsList.clear()
        realm.beginTransaction()
        var results = realm.where(RoomsAvailable::class.java).findAll()
        realm.commitTransaction()
        if (results.size == 0) {
            deviceConnectPresenter.addRoom("Home")
        } else {
            results.forEach { result ->
                val roomsAvailable = AvailableRoomsFormat(result.id, result.room)
                availableRoomsList.add(roomsAvailable)
            }
        }
        var layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        availableRoomsRecyclerView.layoutManager = layoutManager
        availableRoomsAdapter = RoomsAdapter(availableRoomsList, requireContext(), deviceConnectPresenter)
        availableRoomsRecyclerView.adapter = availableRoomsAdapter
    }

    override fun roomNameSelected(roomName: String?) {

        if (roomName.isNullOrEmpty()) {
            roomNameSelected = null
            room_next.setBackgroundColor(resources.getColor(R.color.default_bg))
            room_next.setTextColor(Color.BLACK)
            room_next.isClickable = false
        } else {
            roomNameSelected = roomName.toString()
            room_next.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            room_next.setTextColor(Color.WHITE)
            room_next.isClickable = true
        }
    }

    override fun showAnonymousDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage(getString(R.string.anonymous_details))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.use_anonymous_mode), DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                    ANONYMOUS_MODE = true
                    deviceConnectPresenter.makeConfigRequest()
                })
                .setNegativeButton(getString(R.string.enter_password_button), DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                    anonymous_mode.isChecked = false
                    ANONYMOUS_MODE = false
                })
                .setTitle(getString(R.string.anonymous))
                .create()
                .show()
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
        showWifi.visibility = View.GONE
    }

    override fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
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
                    else {
                        stopProgress()
                        deviceConnectPresenter.availableWifi(wifiList)
                    }
                } else if (p1.action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
                    Timber.d("Wifi changes")
                    if (p1.getParcelableExtra<Parcelable>(WifiManager.EXTRA_NEW_STATE) == SupplicantState.COMPLETED) {
                        Timber.d("Wifi Changes #2")
                        val wifiInfo = mainWifi.connectionInfo
                        if (wifiInfo != null) {
                            val ssid = wifiInfo.ssid
                            Timber.d("WiFi ssid: " + ssid)
                            if (ssid.equals("\"SUSI.AI\"")) {
                                macId = wifiInfo.macAddress
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
        if (showWifiList) {
            Timber.d("Setup Wifi adapter")
            scanDevice.setText(R.string.choose_wifi)
            scanList.remove(getString(R.string.susi_ai_hotspot))
            scanProgress.visibility = View.GONE
            scanHelp.visibility = View.VISIBLE
            deviceList.visibility = View.GONE
            addDeviceButton.visibility = View.GONE
            wifiList.visibility = View.VISIBLE
            showWifi.visibility = View.VISIBLE
            wifiList.layoutManager = LinearLayoutManager(context)
            wifiList.setHasFixedSize(true)
            recyclerAdapter = DevicesAdapter(scanList, deviceConnectPresenter, VIEW_AVAILABLE_WIFI)
            wifiList.adapter = recyclerAdapter
        }
    }

    data class AvailableRoomsFormat(
        val id: Long,
        val room: String?
    )
}
