package org.fossasia.susi.ai.device.connecteddevices

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_connected_device.deviceStatus
import kotlinx.android.synthetic.main.fragment_connected_device.refresh_device_layout
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.device.DeviceActivity
import org.fossasia.susi.ai.device.DeviceActivity.Companion.CONNECT_TO
import org.fossasia.susi.ai.device.DeviceActivity.Companion.DEVICE_DETAILS
import org.fossasia.susi.ai.device.DeviceActivity.Companion.MAC_ID
import org.fossasia.susi.ai.device.DeviceActivity.Companion.TAG_VIEW_DEVICE_FRAGMENT
import org.fossasia.susi.ai.device.connecteddevices.adapters.ConnectedDevicesAdapter
import org.fossasia.susi.ai.device.connecteddevices.contract.IConnectedDevicePresenter
import org.fossasia.susi.ai.device.connecteddevices.contract.IConnectedDeviceView
import org.fossasia.susi.ai.rest.responses.susi.Device
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class ConnectedDeviceFragment : Fragment(), IConnectedDeviceView {

    private lateinit var connectedDeviceRecyclerView: RecyclerView
    private val connectedDevicePresenter: IConnectedDevicePresenter by inject { parametersOf(this) }
    private var connectedDevicesAdapter: RecyclerView.Adapter<*>? = null
    private val connectedDeviceList: ArrayList<Device> = ArrayList()
    private val macIdList: MutableList<String> = ArrayList()

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
        connectedDeviceRecyclerView = rootView.findViewById(R.id.connectedDevices)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getDeviceList()

        refresh_device_layout.setOnRefreshListener {
            onRefresh()
        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun getDeviceList() {
        connectedDevicePresenter.getDevices()
    }

    override fun getConnectedDeviceDetails(deviceResponseMap: Map<String, Device>?) {
        refresh_device_layout.isRefreshing = false

        deviceResponseMap?.forEach { item ->
            connectedDeviceList.add(item.value)
            macIdList.add(item.key)
        }

        if (connectedDeviceList.isNullOrEmpty()) {
            deviceStatus.visibility = View.VISIBLE
            deviceStatus.text = getString(R.string.connected_device_status)
        } else {
            deviceStatus.visibility = View.GONE
            showDevices()
        }
    }

    fun showDevices() {
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        connectedDeviceRecyclerView.layoutManager = layoutManager
        connectedDevicesAdapter = ConnectedDevicesAdapter(connectedDeviceList, connectedDevicePresenter)
        connectedDeviceRecyclerView.adapter = connectedDevicesAdapter
    }

    override fun onRefresh() {
        connectedDeviceList.clear()
        refresh_device_layout.isRefreshing = true
        connectedDevicesAdapter?.notifyDataSetChanged()
        getDeviceList()
    }

    override fun viewDevice(positiion: Int) {
        val intent = Intent(activity, DeviceActivity::class.java)
        intent.putExtra(CONNECT_TO, TAG_VIEW_DEVICE_FRAGMENT)
        intent.putExtra(DEVICE_DETAILS, connectedDeviceList[positiion])
        intent.putExtra(MAC_ID, macIdList[positiion])
        startActivity(intent)
    }
}
