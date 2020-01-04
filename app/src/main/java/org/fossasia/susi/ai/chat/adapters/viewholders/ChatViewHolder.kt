package org.fossasia.susi.ai.chat.adapters.viewholders

import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import kotterknife.bindOptionalView
import kotterknife.bindView
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.ChatActivity
import org.fossasia.susi.ai.chat.adapters.recycleradapters.ChatFeedRecyclerAdapter
import org.fossasia.susi.ai.data.model.ChatMessage
import org.fossasia.susi.ai.helper.Constant
import timber.log.Timber

/**
 * ViewHolder for drawing chat item layout.
 */
class ChatViewHolder(view: View, clickListener: MessageViewHolder.ClickListener, myMessage: Int) :
        MessageViewHolder(view, clickListener) {

    val chatTextView: TextView by bindView(R.id.text)
    private val receivedTick: ImageView? by bindOptionalView(R.id.received_tick)
    val timeStamp: TextView by bindView(R.id.timestamp)
    val backgroundLayout: LinearLayout by bindView(R.id.background_layout)

    /**
     * Inflate ChatView
     *
     * @param model the ChatMessage object
     * @param viewType the viewType
     */
    fun setView(chatModel: ChatMessage?, viewType: Int, context: Context) {
        if (chatModel != null) {
            model = chatModel
            try {
                when (viewType) {
                    ChatFeedRecyclerAdapter.USER_MESSAGE -> {
                        chatTextView.text = chatModel.content
                        timeStamp.text = chatModel.timeStamp
                        if (chatModel.isDelivered) {
                            receivedTick?.setImageResource(R.drawable.ic_check)
                        } else {
                            receivedTick?.setImageResource(R.drawable.ic_clock)
                        }

                        resendMessage?.setOnClickListener {
                            val activity = context
                            if (activity is ChatActivity) {
                                activity.setText(chatModel.content)
                            }
                        }

                        chatTextView.tag = this
                        timeStamp.tag = this
                        receivedTick?.tag = this
                    }
                    ChatFeedRecyclerAdapter.SUSI_MESSAGE -> {
                        val answerText: Spanned = if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Html.fromHtml(chatModel.content, Html.FROM_HTML_MODE_COMPACT)
                        } else {
                            Html.fromHtml(chatModel.content)
                        }
                        if (chatModel.actionType == Constant.ANCHOR) {
                            chatTextView.linksClickable = true
                            chatTextView.movementMethod = LinkMovementMethod.getInstance()
                        } else {
                            chatTextView.linksClickable = false
                        }

                        if (chatModel.skillLocation.isNullOrEmpty()) {
                            thumbsUp.visibility = View.GONE
                            thumbsDown.visibility = View.GONE
                        } else {
                            thumbsUp.visibility = View.VISIBLE
                            thumbsDown.visibility = View.VISIBLE
                        }

                        if (chatModel.isPositiveRated || chatModel.isNegativeRated) {
                            thumbsUp.visibility = View.GONE
                            thumbsDown.visibility = View.GONE
                        } else {
                            thumbsUp.setImageResource(R.drawable.thumbs_up_outline)
                            thumbsDown.setImageResource(R.drawable.thumbs_down_outline)
                        }

                        chatTextView.text = answerText
                        timeStamp.text = chatModel.timeStamp
                        chatTextView.tag = this
                        timeStamp.tag = this

                        thumbsUp.setOnClickListener {
                            Timber.d("%s %s", chatModel.isPositiveRated, chatModel.isNegativeRated)
                            if (!chatModel.isPositiveRated && !chatModel.isNegativeRated) {
                                thumbsUp.setImageResource(R.drawable.thumbs_up_solid)
                                chatModel.skillLocation?.let { location -> rateSusiSkill(Constant.POSITIVE, location, context) }
                                setRating(true, true)
                            }
                        }

                        thumbsDown.setOnClickListener {
                            Timber.d("%s %s", chatModel.isPositiveRated, chatModel.isNegativeRated)
                            if (!chatModel.isPositiveRated && !chatModel.isNegativeRated) {
                                thumbsDown.setImageResource(R.drawable.thumbs_down_solid)
                                chatModel.skillLocation?.let { location -> rateSusiSkill(Constant.NEGATIVE, location, context) }
                                setRating(true, false)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }
}
