package org.fossasia.susi.ai.device.deviceconnect.adapters.recycleradapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.room_recycler_layout.view.room_text
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.device.DeviceActivity
import org.fossasia.susi.ai.device.deviceconnect.DeviceConnectFragment

class RoomsAdapter(private val availableRoomsList: ArrayList<DeviceConnectFragment.AvailableRoomsFormat>, val context: Context?) : RecyclerView.Adapter<RoomsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.room_recycler_layout, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return availableRoomsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
        holder.itemView.room_text.text = availableRoomsList[p1].room
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.room_text.setOnClickListener {
                // val this_activity = context
                val roomNameClicked = availableRoomsList[adapterPosition].room
                var intent = Intent(context, DeviceActivity::class.java)
                intent.putExtra("roomName", roomNameClicked)
                intent.putExtra(DeviceActivity().CONNECT_TO, DeviceActivity().TAG_DEVICE_CONNECT_FRAGMENT)
                context?.startActivity(intent)
            }
        }
    }
}