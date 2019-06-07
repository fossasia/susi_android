package org.fossasia.susi.ai.device.connecteddevices.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.device.connecteddevices.ConnectedDeviceFormat

class ConnectedDevicesAdapter(private val connectedDevicesList: ArrayList<ConnectedDeviceFormat>) : RecyclerView.Adapter<ConnectedDevicesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.device_layout, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return connectedDevicesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
        holder.ssid_name.text = connectedDevicesList[p1].ssid
        holder.setup_option.text = connectedDevicesList[p1].dateTime
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var ssid_name: TextView
        internal var setup_option: TextView

        init {
            ssid_name = itemView.findViewById(R.id.speakerName)
            setup_option = itemView.findViewById(R.id.speakerSetUp)
        }
    }
}