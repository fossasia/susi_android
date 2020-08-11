package org.fossasia.susi.ai.chat.listeners

import android.content.pm.ActivityInfo
import android.support.v4.app.FragmentActivity
import com.google.android.youtube.player.YouTubePlayer
import org.fossasia.susi.ai.chat.YoutubeFragment

class FullscreenListener : YouTubePlayer.OnFullscreenListener {
    private var mContext: FragmentActivity? = null
    private var mFragment: YoutubeFragment? = null

    constructor(context: FragmentActivity?, fragment: YoutubeFragment?) {
        mContext = context
        mFragment = fragment
    }

    override fun onFullscreen(p0: Boolean) {
        if (!p0) {
            mContext?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
}
