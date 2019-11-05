package org.fossasia.susi.ai.chat

import android.content.Context
import android.content.Intent
import org.fossasia.susi.ai.helper.Constant

class YoutubeVid(val context: Context) : IYoutubeVid {

    override fun playYoutubeVid(videoId: String) {
        val intent = Intent(context, YouTubeActivity::class.java)
        intent.putExtra(Constant.VIDEO_ID, videoId)
        context.startActivity(intent)
    }
}
