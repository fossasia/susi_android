package org.fossasia.susi.ai.device.viewdevice

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_view_device.spk_room
import kotlinx.android.synthetic.main.fragment_view_device.spk_location
import kotlinx.android.synthetic.main.fragment_view_device.spk_name
import kotlinx.android.synthetic.main.fragment_view_device.spk_macid
import kotlinx.android.synthetic.main.fragment_view_device.spk_email
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.device.viewdevice.contract.IViewDeviceView
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.PrefManager
import org.fossasia.susi.ai.rest.responses.susi.Device

class ViewDeviceFragment : Fragment(), IViewDeviceView {

    private var macId: String? = null
    private var device: Device? = null
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
        device = extras?.get("deviceDetails") as Device?
        viewDevicePresenter = ViewDevicePresenter()
        viewDevicePresenter.onAttach(this, macId, device)

        viewDetails()
    }

    // Function to show the device information
    override fun viewDetails() {
        spk_email.text = PrefManager.getStringSet(Constant.SAVED_EMAIL)?.iterator()?.next().toString()
        spk_macid.text = macId
        spk_room.text = device?.room
        spk_name.text = device?.name
        spk_location.text = device?.geolocation?.latitude + ", " + device?.geolocation?.longitude
    }
}
