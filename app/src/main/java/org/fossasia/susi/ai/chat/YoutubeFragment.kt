package org.fossasia.susi.ai.chat

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.listeners.FullscreenListener

class YoutubeFragment : Fragment() {
    private var API_KEY: String? = null
    private var m_RootView: View? = null
    private var m_StopButton: View? = null
    var YPlayer: YouTubePlayer? = null
    private var VIDEO: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView = inflater.inflate(R.layout.fragment_youtube, container, false)
        m_RootView = rootView
        m_StopButton = m_RootView?.findViewById(R.id.youtube_stop_btn)
        var bundle = activity?.packageManager?.getApplicationInfo(activity?.applicationContext?.packageName, PackageManager.GET_META_DATA)?.metaData
        API_KEY = bundle?.getString("com.google.android.youtube.API_KEY")
        if (arguments != null) {
            VIDEO = arguments!!.getString("video")
        }
        m_StopButton?.setOnClickListener({
            YPlayer?.release()
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        })
        initYoutube()
        return rootView
    }

    private fun initYoutube() {
        val youTubePlayerFragment: YouTubePlayerSupportFragment =
                YouTubePlayerSupportFragment.newInstance()
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction.add(R.id.youtube_fragment, youTubePlayerFragment as Fragment)
        transaction.commit()
        youTubePlayerFragment.initialize(API_KEY, object : YouTubePlayer.OnInitializedListener {

            override fun onInitializationSuccess(
                provider: YouTubePlayer.Provider?,
                player: YouTubePlayer,
                wasRestored: Boolean
            ) {
                if (!wasRestored) {
                    YPlayer = player
                    YPlayer?.loadVideo(VIDEO)
                    YPlayer?.setOnFullscreenListener(FullscreenListener(activity, this@YoutubeFragment))
                    YPlayer?.play()
                }
            }

            override fun onInitializationFailure(
                arg0: YouTubePlayer.Provider?,
                arg1: YouTubeInitializationResult?
            ) {
            }
        })
    }
    companion object {
        fun newInstance() = YoutubeFragment()
    }
}
