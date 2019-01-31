package org.fossasia.susi.ai.chat.adapters.viewholders

import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.ImageView

import com.squareup.picasso.Picasso

import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.YoutubeVid
import org.fossasia.susi.ai.data.model.ChatMessage

import kotterknife.bindView
import timber.log.Timber

class YoutubeVideoViewHolder(view: View, clickListener: MessageViewHolder.ClickListener) : MessageViewHolder(view, clickListener) {

    private val playerView: ImageView by bindView(R.id.youtube_view)
    private val playBtn: ImageView by bindView(R.id.play_video)
    private var model: ChatMessage? = null
    private var videoId: String? = null

    fun setPlayerView(model: ChatMessage?) {
        this.model = model

        if (model != null) {
            try {
                videoId = model.identifier
                val imgUrl = "http://img.youtube.com/vi/$videoId/0.jpg"

                ContextCompat.getDrawable(itemView.context, R.drawable.ic_susi)?.let {
                    Picasso.get()
                            .load(imgUrl)
                            .placeholder(it)
                            .into(playerView)
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }

        val youtubeVid = YoutubeVid(itemView.context)
        playBtn.setOnClickListener { videoId?.let { id -> youtubeVid.playYoutubeVid(id) } }
    }
}
