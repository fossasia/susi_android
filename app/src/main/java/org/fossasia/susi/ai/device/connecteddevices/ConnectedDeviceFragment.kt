package org.fossasia.susi.ai.device.connecteddevices

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_connected_device.*
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.data.model.ConnectedDevice
import org.fossasia.susi.ai.device.DeviceActivity
import org.fossasia.susi.ai.device.connecteddevices.adapters.ConnectedDevicesAdapter

class ConnectedDeviceFragment : Fragment() {

    lateinit var realm: Realm
    lateinit var connectedDeviceRecyclerView: RecyclerView
    private var connectedDevicesAdapter: RecyclerView.Adapter<*>? = null
    val connectedDeviceList: ArrayList<ConnectedDeviceFormat> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_connected_device, container, false)
        val thisactivity = activity
        (thisactivity as DeviceActivity).title = getString(R.string.connected_device_fragment)
        realm = Realm.getDefaultInstance()
        connectedDeviceRecyclerView = rootView.findViewById(R.id.connectedDevices)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        realm.beginTransaction()

        var results = realm.where(ConnectedDevice::class.java).findAll()

        if (results.size == 0) {
            deviceStatus.visibility = View.VISIBLE
        } else {

            // Fill the connectedDevice list with the datas received
            results.forEach { result ->
                val connectedDevice = ConnectedDeviceFormat()
                connectedDevice.id = result.id
                connectedDevice.ssid = result.ssid
                connectedDevice.dateTime = result.datetime
                connectedDeviceList.add(connectedDevice)
            }

            // Calling the recyclerview if there is connected devices
            deviceStatus.visibility = View.GONE
            var layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            connectedDeviceRecyclerView.layoutManager = layoutManager
            connectedDevicesAdapter = ConnectedDevicesAdapter(connectedDeviceList)
            connectedDeviceRecyclerView.adapter = connectedDevicesAdapter
        }
        realm.commitTransaction()
        super.onViewCreated(view, savedInstanceState)
    }
}

class ConnectedDeviceFormat {
    var id: Long ? = null
    var ssid: String? = null
    var dateTime: String? = null
}
