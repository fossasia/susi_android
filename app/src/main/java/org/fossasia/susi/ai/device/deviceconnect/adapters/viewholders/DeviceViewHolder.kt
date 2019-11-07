package org.fossasia.susi.ai.device.deviceconnect.adapters.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import kotterknife.bindView
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.device.deviceconnect.DeviceConnectPresenter

class DeviceViewHolder(itemView: View, private var devicePresenter: DeviceConnectPresenter) : RecyclerView.ViewHolder(itemView) {

    val speakerName: TextView by bindView(R.id.speakerName)
    val setUp: TextView by bindView(R.id.speakerSetUp)

    // Initializing the onClick function
    init {
        setUp.setOnClickListener {
            onClick()
        }
    }

    fun onClick() {
        val ssid = speakerName.text.toString()
        devicePresenter.connectToDevice(ssid)
    }
}
