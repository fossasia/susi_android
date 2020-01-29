package org.fossasia.susi.ai.device.deviceconnect.adapters.viewholders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotterknife.bindView
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.device.deviceconnect.DeviceConnectPresenter

class DeviceViewHolder(itemView: View, private var devicePresenter: DeviceConnectPresenter) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

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
