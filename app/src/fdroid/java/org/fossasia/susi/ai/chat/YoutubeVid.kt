package org.fossasia.susi.ai.chat

import android.content.Context
import android.content.Intent
import android.net.Uri

class YoutubeVid(val context: Context) : IYoutubeVid {

    override fun playYoutubeVid(videoId: String) {
        val url = "http://www.youtube.com/watch?v=$videoId"
        val videoClient = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(videoClient)
    }
}
