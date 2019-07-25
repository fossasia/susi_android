package org.fossasia.susi.ai.device.viewdevice

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.edit_name_layout.*
import kotlinx.android.synthetic.main.fragment_view_device.*
import kotlinx.android.synthetic.main.view_device_layout.spk_location
import kotlinx.android.synthetic.main.view_device_layout.spk_email
import kotlinx.android.synthetic.main.view_device_layout.spk_macid
import kotlinx.android.synthetic.main.view_device_layout.spk_room
import kotlinx.android.synthetic.main.view_device_layout.spk_name
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.device.viewdevice.contract.IViewDeviceView
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.PrefManager
import org.fossasia.susi.ai.rest.responses.susi.Device

class ViewDeviceFragment : Fragment(), IViewDeviceView {

    private var macId: String? = null
    private lateinit var name: String
    private lateinit var room: String
    private lateinit var latitude: String
    private lateinit var longitude: String

    private lateinit var viewDevicePresenter: ViewDevicePresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_view_device, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val extras = activity?.intent?.extras
        macId = extras?.getString("macId")
        val device = extras?.get("deviceDetails") as Device?
        name = device?.name.toString()
        room = device?.room.toString()
        latitude = device?.geolocation?.latitude.toString()
        longitude = device?.geolocation?.longitude.toString()

        viewDevicePresenter = ViewDevicePresenter()
        viewDevicePresenter.onAttach(this, macId, device)

        initialSetUp()
    }

    override fun initialSetUp() {
        viewDetails()

        spk_name.setOnClickListener {
            changeSpeakerName()
        }
    }

    // Function to show the device information
    override fun viewDetails() {
        view_device.visibility = View.VISIBLE
        change_name.visibility = View.GONE
        spk_email.text = PrefManager.getStringSet(Constant.SAVED_EMAIL)?.iterator()?.next().toString()
        spk_macid.text = macId
        spk_room.text = room
        spk_name.text = name
        spk_location.text = latitude + ", " + longitude
    }

    override fun changeSpeakerName() {
        change_name.visibility = View.VISIBLE
        view_device.visibility = View.GONE

        name_cancel.setOnClickListener {
            viewDetails()
        }

        name_save.setOnClickListener {
            // For testing purpose
            name = name_new.text.toString()
            viewDetails()
        }
    }
}
