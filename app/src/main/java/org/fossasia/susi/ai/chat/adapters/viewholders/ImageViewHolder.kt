package org.fossasia.susi.ai.chat.adapters.viewholders

import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import butterknife.ButterKnife
import com.squareup.picasso.Picasso
import kotterknife.bindView
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.data.model.ChatMessage
import org.fossasia.susi.ai.helper.Constant
import timber.log.Timber

/**
 * ViewHolder for drawing image item layout.
 */
class ImageViewHolder(itemView: View, clickListener: ClickListener) : MessageViewHolder(itemView, clickListener) {

    private val imageView: ImageView by bindView(R.id.image_response)
    val timeStamp: TextView by bindView(R.id.timestamp)
    private var imageURL: String? = null

    init {
        ButterKnife.bind(this, itemView)
    }

    fun setView(chatModel: ChatMessage?) {
        model = chatModel

        if (chatModel != null) {
            imageURL = model?.content
            try {
                ContextCompat.getDrawable(itemView.context, R.drawable.ic_susi)?.let {
                    Picasso.get()
                            .load(imageURL)
                            .placeholder(it)
                            .into(imageView)
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }

        imageView.setOnClickListener {
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(itemView.context, Uri.parse(imageURL))
        }

        if (chatModel?.skillLocation.isNullOrEmpty()) {
            thumbsUp.visibility = View.GONE
            thumbsDown.visibility = View.GONE
        } else {
            thumbsUp.visibility = View.VISIBLE
            thumbsDown.visibility = View.VISIBLE
        }

        if (chatModel != null && (chatModel.isPositiveRated || chatModel.isNegativeRated)) {
            thumbsUp.visibility = View.GONE
            thumbsDown.visibility = View.GONE
        } else {
            thumbsUp.setImageResource(R.drawable.thumbs_up_outline)
            thumbsDown.setImageResource(R.drawable.thumbs_down_outline)
        }

        timeStamp.text = chatModel?.timeStamp

        thumbsUp.setOnClickListener {
            Timber.d("%s %s", chatModel?.isPositiveRated, chatModel?.isNegativeRated)
            if (chatModel != null && !chatModel.isPositiveRated && !chatModel.isNegativeRated) {
                thumbsUp.setImageResource(R.drawable.thumbs_up_solid)
                chatModel.skillLocation?.let { location -> rateSusiSkill(Constant.POSITIVE, location, itemView.context) }
                setRating(true, true)
            }
        }

        thumbsDown.setOnClickListener {
            Timber.d("%s %s", chatModel?.isPositiveRated, chatModel?.isNegativeRated)
            if (chatModel != null && !chatModel.isPositiveRated && !chatModel.isNegativeRated) {
                thumbsDown.setImageResource(R.drawable.thumbs_down_solid)
                chatModel.skillLocation?.let { location -> rateSusiSkill(Constant.NEGATIVE, location, itemView.context) }
                setRating(true, false)
            }
        }
    }
}
