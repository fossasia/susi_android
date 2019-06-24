package org.fossasia.susi.ai.device.connecteddevices

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_connected_device.*
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.device.connecteddevices.adapters.ConnectedDevicesAdapter
import org.fossasia.susi.ai.device.connecteddevices.contract.IConnectedDevicePresenter
import org.fossasia.susi.ai.rest.responses.susi.Device

class ConnectedDeviceFragment : Fragment() {

    lateinit var connectedDeviceRecyclerView: RecyclerView
    private lateinit var connectedDevicePresenter: IConnectedDevicePresenter
    private var connectedDevicesAdapter: RecyclerView.Adapter<*>? = null
    private val connectedDeviceList: ArrayList<ConnectedDeviceFormat> = ArrayList()
    private val connectedDevices: List<Device> = ArrayList()

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
        connectedDevicePresenter.getDevices()

        deviceStatus.visibility = View.GONE
        showDevices()

        super.onViewCreated(view, savedInstanceState)
    }

    fun showDevices() {
        var layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        connectedDeviceRecyclerView.layoutManager = layoutManager
        connectedDevicesAdapter = ConnectedDevicesAdapter(connectedDeviceList)
        connectedDeviceRecyclerView.adapter = connectedDevicesAdapter
    }
}

class ConnectedDeviceFormat {
    var id: Long ? = null
    var ssid: String? = null
}
