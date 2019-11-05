package org.fossasia.susi.ai.device.viewdevice.adapters

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
import org.fossasia.susi.ai.device.viewdevice.ViewDeviceFragment
import org.fossasia.susi.ai.device.viewdevice.contract.IViewDevicePresenter

class ShowRoomsAdapter(
    private val availableRoomsList: ArrayList<ViewDeviceFragment.AvailableRoomsFormat>,
    val context: Context,
    private val viewDevicePresenter: IViewDevicePresenter
) :
    RecyclerView.Adapter<ShowRoomsAdapter.ViewHolder>() {

    private var selectedIndex: Int = -1

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.room_recycler_layout, viewGroup, false)
        return ViewHolder(view)
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

    override fun getItemCount(): Int {
        return availableRoomsList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.delete_room.setOnClickListener {
                viewDevicePresenter.deleteRoom(availableRoomsList[adapterPosition].room)
            }

            var roomNameClicked: String? = null
            itemView.room_text.setOnClickListener {
                if (selectedIndex == adapterPosition) {
                    selectedIndex = -1
                    roomNameClicked = null
                } else {
                    selectedIndex = adapterPosition
                    roomNameClicked = availableRoomsList[selectedIndex].room
                }
                viewDevicePresenter.selectedRoom(roomNameClicked)
                notifyDataSetChanged()
            }
        }
    }
}
