package org.fossasia.susi.ai.device.managedevices.susimusicservice

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.fragment_susi_music.*
import org.fossasia.susi.ai.R

class SusiMusicFragment : Fragment() {

    private lateinit var musicServerIp: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_susi_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        musicServerIp = "https://github.com/fossasia/susi_android"
        susi_music_view.webViewClient = WebViewClient()
        susi_music_view.loadUrl(musicServerIp)
    }
}
