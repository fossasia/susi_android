package org.fossasia.susi.ai.device.managedevices.susimusicservice

import android.content.Context
import android.net.wifi.WifiManager
import org.fossasia.susi.ai.device.managedevices.susimusicservice.contract.ISusiMusicFragmentView
import org.fossasia.susi.ai.device.managedevices.susimusicservice.contract.ISusiMusicServicePresenter

class SusiMusicPresenter(context: Context, manager: WifiManager) : ISusiMusicServicePresenter {
    private var susiMusicView: ISusiMusicFragmentView? = null

    override fun onAttach(susiMusicView: ISusiMusicFragmentView) {
        this.susiMusicView = susiMusicView
    }
}