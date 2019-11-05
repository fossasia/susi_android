package org.fossasia.susi.ai.device.deviceconnect.adapters.recycleradapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.room_recycler_layout.view.delete_room
import kotlinx.android.synthetic.main.room_recycler_layout.view.image_tick
import kotlinx.android.synthetic.main.room_recycler_layout.view.room_text
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.device.deviceconnect.DeviceConnectFragment
import org.fossasia.susi.ai.device.deviceconnect.contract.IDeviceConnectPresenter

class RoomsAdapter(private val availableRoomsList: ArrayList<DeviceConnectFragment.AvailableRoomsFormat>, val context: Context, private val deviceConnectPresenter: IDeviceConnectPresenter) : RecyclerView.Adapter<RoomsAdapter.ViewHolder>() {

    private var selectedIndex: Int = -1
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.room_recycler_layout, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return availableRoomsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
        if (p1 == selectedIndex) {
            holder.itemView.image_tick.background = ContextCompat.getDrawable(context, R.drawable.ic_home_blue_24dp)
            holder.itemView.delete_room.background = ContextCompat.getDrawable(context, R.drawable.ic_delete_blue_24dp)
        } else {
            holder.itemView.image_tick.background = ContextCompat.getDrawable(context, R.drawable.ic_home_black_24dp)
            holder.itemView.delete_room.background = ContextCompat.getDrawable(context, R.drawable.ic_delete_black_24dp)
        }
        holder.itemView.room_text.text = availableRoomsList[p1].room
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            var roomNameClicked: String? = null
            itemView.room_text.setOnClickListener {
                if (selectedIndex == adapterPosition) {
                    selectedIndex = -1
                    roomNameClicked = null
                } else {
                    selectedIndex = adapterPosition
                    roomNameClicked = availableRoomsList[selectedIndex].room
                }
                deviceConnectPresenter.selectedRoom(roomNameClicked)
                notifyDataSetChanged()
            }

            itemView.delete_room.setOnClickListener {
                deviceConnectPresenter.deleteRoom(availableRoomsList[adapterPosition].room)
            }
        }
    }
}
