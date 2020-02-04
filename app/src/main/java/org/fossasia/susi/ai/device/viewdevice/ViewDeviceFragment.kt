package org.fossasia.susi.ai.device.viewdevice

import android.app.ProgressDialog
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.realm.Realm
import kotlinx.android.synthetic.main.edit_name_layout.name_cancel
import kotlinx.android.synthetic.main.edit_name_layout.name_new
import kotlinx.android.synthetic.main.edit_name_layout.name_save
import kotlinx.android.synthetic.main.edit_room_layout.add_room
import kotlinx.android.synthetic.main.edit_room_layout.edit_room
import kotlinx.android.synthetic.main.edit_room_layout.edit_room_previous
import kotlinx.android.synthetic.main.edit_room_layout.edit_room_save
import kotlinx.android.synthetic.main.fragment_view_device.change_name
import kotlinx.android.synthetic.main.fragment_view_device.change_room
import kotlinx.android.synthetic.main.fragment_view_device.view_device
import kotlinx.android.synthetic.main.view_device_layout.spk_email
import kotlinx.android.synthetic.main.view_device_layout.spk_location
import kotlinx.android.synthetic.main.view_device_layout.spk_macid
import kotlinx.android.synthetic.main.view_device_layout.spk_name
import kotlinx.android.synthetic.main.view_device_layout.spk_room
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.data.model.RoomsAvailable
import org.fossasia.susi.ai.dataclasses.AddDeviceQuery
import org.fossasia.susi.ai.device.DeviceActivity.Companion.DEVICE_DETAILS
import org.fossasia.susi.ai.device.DeviceActivity.Companion.MAC_ID
import org.fossasia.susi.ai.device.viewdevice.adapters.ShowRoomsAdapter
import org.fossasia.susi.ai.device.viewdevice.contract.IViewDevicePresenter
import org.fossasia.susi.ai.device.viewdevice.contract.IViewDeviceView
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.PrefManager
import org.fossasia.susi.ai.rest.responses.susi.Device
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class ViewDeviceFragment : Fragment(), IViewDeviceView {

    private lateinit var macId: String
    private lateinit var name: String
    private lateinit var room: String
    private lateinit var latitude: String
    private lateinit var longitude: String
    private lateinit var realm: Realm
    private val availableRoomsList: ArrayList<AvailableRoomsFormat> = ArrayList()
    private lateinit var availableRoomsRecyclerView: RecyclerView
    private var availableRoomsAdapter: RecyclerView.Adapter<*>? = null
    private var roomNameSelected: String? = null
    private val viewDevicePresenter: IViewDevicePresenter by inject { parametersOf(this) }
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_view_device, container, false)
        availableRoomsRecyclerView = rootView.findViewById(R.id.edit_rooms_available)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val extras = activity?.intent?.extras
        macId = extras?.getString(MAC_ID).toString()
        val device = extras?.get(DEVICE_DETAILS) as Device?
        name = device?.name.toString()
        room = device?.room.toString()
        latitude = device?.geolocation?.latitude.toString()
        longitude = device?.geolocation?.longitude.toString()
        realm = Realm.getDefaultInstance()
        progressDialog = ProgressDialog(context)

        viewDevicePresenter.onAttach(this, macId, device, requireContext())

        initialSetUp()
    }

    override fun initialSetUp() {
        viewDetails()

        spk_name.setOnClickListener {
            changeSpeakerName()
        }

        spk_room.setOnClickListener {
            changeRoomName()
        }
    }

    // Function to show the device information
    override fun viewDetails() {
        view_device.visibility = View.VISIBLE
        change_name.visibility = View.GONE
        change_room.visibility = View.GONE
        spk_email.text = PrefManager.getStringSet(Constant.SAVED_EMAIL)?.iterator()?.next().toString()
        spk_macid.text = macId
        spk_room.text = room
        spk_name.text = name
        spk_location.text = latitude + ", " + longitude
    }

    override fun changeSpeakerName() {
        change_name.visibility = View.VISIBLE
        view_device.visibility = View.GONE
        change_room.visibility = View.GONE

        name_cancel.setOnClickListener {
            viewDetails()
        }

        name_save.setOnClickListener {
            name = name_new.text.toString()
            addDevice()
        }
    }

    override fun changeRoomName() {
        change_name.visibility = View.GONE
        view_device.visibility = View.GONE
        change_room.visibility = View.VISIBLE
        showRooms()

        edit_room_previous.setOnClickListener {
            viewDetails()
        }

        edit_room_save.setOnClickListener {
            room = roomNameSelected.toString()
            addDevice()
        }

        add_room.setOnClickListener {
            val room = edit_room.text.toString()
            if (room.length > 0) {
                viewDevicePresenter.addRoom(room)
                edit_room.setText("")
            } else {
                showToast(getString(R.string.room_empty))
            }
        }
    }

    override fun showRooms() {
        availableRoomsList.clear()
        realm.beginTransaction()
        var results = realm.where(RoomsAvailable::class.java).findAll()
        realm.commitTransaction()
        if (results.size == 0) {
            viewDevicePresenter.addRoom("Home")
        } else {
            results.forEach { result ->
                val roomsAvailable = AvailableRoomsFormat(result.id, result.room)
                availableRoomsList.add(roomsAvailable)
            }
        }
        var layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        availableRoomsRecyclerView.layoutManager = layoutManager
        availableRoomsAdapter = ShowRoomsAdapter(availableRoomsList, requireContext(), viewDevicePresenter)
        availableRoomsRecyclerView.adapter = availableRoomsAdapter
    }

    override fun roomNameSelected(roomName: String?) {

        if (roomName.isNullOrEmpty()) {
            roomNameSelected = null
            edit_room_save.setBackgroundColor(resources.getColor(R.color.default_bg))
            edit_room_save.setTextColor(Color.BLACK)
            edit_room_save.isClickable = false
        } else {
            roomNameSelected = roomName.toString()
            edit_room_save.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            edit_room_save.setTextColor(Color.WHITE)
            edit_room_save.isClickable = true
        }
    }

    override fun addDevice() {
        val query = AddDeviceQuery(macId, name, room, latitude, longitude)
        viewDevicePresenter.updateDevice(query)
        showProgressbar()
    }

    override fun showProgressbar() {
        progressDialog.setMessage(getString(R.string.updating_device))
        progressDialog.show()
    }

    override fun stopProgressbar() {
        progressDialog.dismiss()
    }

    override fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    data class AvailableRoomsFormat(
        val id: Long,
        val room: String?
    )
}
