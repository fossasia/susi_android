package org.fossasia.susi.ai.device.viewdevice

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.device.viewdevice.contract.IViewDeviceView
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
        Toast.makeText(context, device?.name, Toast.LENGTH_SHORT).show()
        viewDevicePresenter = ViewDevicePresenter()
        viewDevicePresenter.onAttach(this, macId, device)
    }
}
