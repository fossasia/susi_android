package org.fossasia.susi.ai.device.viewdevice.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.room_recycler_layout.view.*
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.device.viewdevice.ViewDeviceFragment
import org.fossasia.susi.ai.device.viewdevice.contract.IViewDevicePresenter

class ShowRoomsAdapter(private val availableRoomsList: ArrayList<ViewDeviceFragment.AvailableRoomsFormat>, val context: Context, private val viewDevicePresenter: IViewDevicePresenter) : RecyclerView.Adapter<ShowRoomsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.room_recycler_layout, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
        holder.itemView.room_text.text = availableRoomsList[p1].room
    }

    override fun getItemCount(): Int {
        return availableRoomsList.size
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            // To be implemented
        }
    }
}