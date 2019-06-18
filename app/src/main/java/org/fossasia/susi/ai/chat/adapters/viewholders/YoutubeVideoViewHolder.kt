package org.fossasia.susi.ai.chat.adapters.viewholders

import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.ImageView
import com.squareup.picasso.Picasso
import kotterknife.bindView
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.YoutubeVid
import org.fossasia.susi.ai.data.model.ChatMessage
import timber.log.Timber

/**
 * ViewHolder for drawing youtube video item layout.
 */
class YoutubeVideoViewHolder(
    view: View,
    clickListener: MessageViewHolder.ClickListener
) :
    MessageViewHolder(view, clickListener) {

    private val playerView: ImageView by bindView(R.id.youtube_view)
    private val playButton: ImageView by bindView(R.id.play_video)
    private var videoId: String? = null

    fun setPlayerView(chatModel: ChatMessage?) {
        model = chatModel

        if (chatModel != null) {
            try {
                videoId = chatModel.identifier
                val imageUrl = "http://img.youtube.com/vi/$videoId/0.jpg"

                ContextCompat.getDrawable(itemView.context, R.drawable.ic_susi)?.let {
                    Picasso.get()
                            .load(imageUrl)
                            .placeholder(it)
                            .into(playerView)
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }

        val youtubeVid = YoutubeVid(itemView.context)
        playButton.setOnClickListener { videoId?.let { id -> youtubeVid.playYoutubeVid(id) } }
    }
}
