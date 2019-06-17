package org.fossasia.susi.ai.device.deviceconnect.adapters.recycleradapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.device.deviceconnect.DeviceConnectFragment

class RoomsAdapter(private val connectedDevicesList: ArrayList<DeviceConnectFragment.AvailableRoomsFormat>) : RecyclerView.Adapter<RoomsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.room_recycler_layout, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return connectedDevicesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
        holder.room.text = connectedDevicesList[p1].room
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var room: TextView

        init {
            room = itemView.findViewById(R.id.room_text)
        }
    }
}