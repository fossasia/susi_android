package org.fossasia.susi.ai.device.connecteddevices.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.device.connecteddevices.contract.IConnectedDevicePresenter
import org.fossasia.susi.ai.rest.responses.susi.Device

class ConnectedDevicesAdapter(private val connectedDevicesList: ArrayList<Device>, val connectedDevicePresenter: IConnectedDevicePresenter) : RecyclerView.Adapter<ConnectedDevicesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_speaker, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return connectedDevicesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
        holder.speaker_room.text = connectedDevicesList[p1].room.toString().trim() + " Speaker"
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var speaker_room: TextView

        init {
            speaker_room = itemView.findViewById(R.id.speaker_room)
            itemView.setOnClickListener {
                connectedDevicePresenter.openViewDevice(adapterPosition)
            }
        }
    }
}
