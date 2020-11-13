package org.fossasia.susi.ai.device.managedevices

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_music_servers.susi_music
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.device.managedevices.susimusicservice.SusiMusicFragment
import org.fossasia.susi.ai.device.managedevices.susimusicservice.SusiMusicPresenter
import org.fossasia.susi.ai.device.managedevices.susimusicservice.contract.ISusiMusicFragmentView
import org.fossasia.susi.ai.device.managedevices.susimusicservice.contract.ISusiMusicServicePresenter

class MusicServersFragment : Fragment(), ISusiMusicFragmentView {
    lateinit var mainWifi: WifiManager
    lateinit var musicServicePresenter: ISusiMusicServicePresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_music_servers, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainWifi = context?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        musicServicePresenter = SusiMusicPresenter(requireContext(), mainWifi)
        musicServicePresenter.onAttach(this)

        susi_music.setOnClickListener {
            val susiMusicFragment = SusiMusicFragment()
            fragmentManager?.beginTransaction()
                    ?.replace(R.id.manage_device_container, susiMusicFragment)
                    ?.commit()
        }
    }
}
