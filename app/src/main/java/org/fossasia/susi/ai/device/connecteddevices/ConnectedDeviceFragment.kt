package org.fossasia.susi.ai.device.connecteddevices

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_connected_device.deviceStatus
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.device.connecteddevices.adapters.ConnectedDevicesAdapter
import org.fossasia.susi.ai.device.connecteddevices.contract.IConnectedDevicePresenter
import org.fossasia.susi.ai.device.connecteddevices.contract.IConnectedDeviceView
import org.fossasia.susi.ai.rest.responses.susi.Device

class ConnectedDeviceFragment : Fragment(), IConnectedDeviceView {

    private lateinit var connectedDeviceRecyclerView: RecyclerView
    private lateinit var connectedDevicePresenter: IConnectedDevicePresenter
    private var connectedDevicesAdapter: RecyclerView.Adapter<*>? = null
    private val connectedDeviceList: ArrayList<Device> = ArrayList()
    private val macIdList: ArrayList<String> = ArrayList()

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
        connectedDevicePresenter = ConnectedDevicePresenter(this)
        connectedDevicePresenter.onAttach(this)
        connectedDevicePresenter.getDevices()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun getConnectedDeviceDetails(deviceResponseMap: Map<String, Device>?) {

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
        connectedDevicesAdapter = ConnectedDevicesAdapter(connectedDeviceList)
        connectedDeviceRecyclerView.adapter = connectedDevicesAdapter
    }
}