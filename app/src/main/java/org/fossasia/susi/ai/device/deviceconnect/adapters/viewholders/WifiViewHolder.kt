package org.fossasia.susi.ai.device.deviceconnect.adapters.viewholders

import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView

import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.data.UtilModel
import org.fossasia.susi.ai.device.deviceconnect.DeviceConnectPresenter

import kotterknife.bindView

class WifiViewHolder(itemView: View, private var devicePresenter: DeviceConnectPresenter) : RecyclerView.ViewHolder(itemView) {

    val wifiName: TextView by bindView(R.id.wifi_name)
    val layout: LinearLayout by bindView(R.id.layout_wifi)

    init {
        layout.setOnClickListener {
            onClick()
        }
    }

    fun onClick() {
        val utilModel = UtilModel(itemView.context)
        val view = LayoutInflater.from(itemView.context).inflate(R.layout.get_password, null)
        val alertDialog = AlertDialog.Builder(itemView.context).create()
        alertDialog.setTitle(utilModel.getString(R.string.enter_password) + wifiName.text.toString())
        alertDialog.setCancelable(false)

        val password = view.findViewById<EditText>(R.id.edt_pass)

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, utilModel.getString(R.string.next)) { dialog, which -> devicePresenter.makeWifiRequest(wifiName.text.toString(), password.text.toString()) }

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, utilModel.getString(R.string.cancel)) { dialog, which -> alertDialog.dismiss() }

        alertDialog.setView(view)
        alertDialog.show()
    }
}
