package org.fossasia.susi.ai.device.deviceconnect.adapters.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import kotterknife.bindView
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.device.deviceconnect.DeviceConnectPresenter

class WifiViewHolder(itemView: View, private var devicePresenter: DeviceConnectPresenter) : RecyclerView.ViewHolder(itemView) {

    val wifiName: TextView by bindView(R.id.wifi_name)
    val layout: LinearLayout by bindView(R.id.layout_wifi)

    init {
        layout.setOnClickListener {
            onClick()
        }
    }

    fun onClick() {
        devicePresenter.selectedWifi(wifiName.text.toString())
    }
}
