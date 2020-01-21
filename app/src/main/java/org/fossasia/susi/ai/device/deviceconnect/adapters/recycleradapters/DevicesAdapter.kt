package org.fossasia.susi.ai.device.deviceconnect.adapters.recycleradapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.device.deviceconnect.DeviceConnectPresenter
import org.fossasia.susi.ai.device.deviceconnect.adapters.viewholders.DeviceViewHolder
import org.fossasia.susi.ai.device.deviceconnect.adapters.viewholders.WifiViewHolder
import org.fossasia.susi.ai.device.deviceconnect.contract.IDeviceConnectPresenter

class DevicesAdapter(private val itemList: List<String>, private val devicePresenter: IDeviceConnectPresenter, private val viewCode: Int) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        return if (viewCode == 1) {
            val deviceLayout = LayoutInflater.from(parent.context).inflate(R.layout.device_layout, parent, false)
            DeviceViewHolder(deviceLayout, devicePresenter as DeviceConnectPresenter)
        } else {
            val layoutWifiItem = LayoutInflater.from(parent.context).inflate(R.layout.layout_wifi_item, parent, false)
            WifiViewHolder(layoutWifiItem, devicePresenter as DeviceConnectPresenter)
        }
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        if (holder is DeviceViewHolder) {
            val ssid = itemList[position]
            holder.speakerName.text = ssid
        } else if (holder is WifiViewHolder) {
            holder.wifiName.text = itemList[position]
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}
