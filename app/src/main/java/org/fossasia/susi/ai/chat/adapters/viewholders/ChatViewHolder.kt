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
import android.widget.Toast

import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.ParseSusiResponseHelper
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.data.model.ChatMessage
import org.fossasia.susi.ai.rest.ClientBuilder
import org.fossasia.susi.ai.rest.responses.susi.SkillRatingResponse

import io.realm.Realm
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

import kotterknife.bindOptionalView
import kotterknife.bindView
import org.fossasia.susi.ai.chat.adapters.recycleradapters.ChatFeedRecyclerAdapter

class ChatViewHolder(view: View, clickListener: MessageViewHolder.ClickListener, myMessage: Int) : MessageViewHolder(view, clickListener) {

    val chatTextView: TextView by bindView(R.id.text)
    val timeStamp: TextView by bindView(R.id.timestamp)
    val backgroundLayout: LinearLayout by bindView(R.id.background_layout)
    val receivedTick: ImageView? by bindOptionalView(R.id.received_tick)
    val thumbsUp: ImageView? by bindOptionalView(R.id.thumbs_up)
    val thumbsDown: ImageView? by bindOptionalView(R.id.thumbs_down)

    private var model: ChatMessage? = null

    /**
     * Inflate ChatView
     *
     * @param model the ChatMessage object
     * @param viewType the viewType
     */
    fun setView(model: ChatMessage?, viewType: Int, context: Context) {
        if (model != null) {
            this.model = model
            try {
                when (viewType) {
                    ChatFeedRecyclerAdapter.USER_MESSAGE -> {
                        chatTextView.text = model.content
                        timeStamp.text = model.timeStamp
                        if (model.isDelivered) {
                            receivedTick?.setImageResource(R.drawable.ic_check)
                        } else {
                            receivedTick?.setImageResource(R.drawable.ic_clock)
                        }

                        chatTextView.tag = this
                        timeStamp.tag = this
                        receivedTick?.tag = this
                    }
                    ChatFeedRecyclerAdapter.SUSI_MESSAGE -> {
                        val answerText: Spanned = if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Html.fromHtml(model.content, Html.FROM_HTML_MODE_COMPACT)
                        } else {
                            Html.fromHtml(model.content)
                        }
                        if (model.actionType == Constant.ANCHOR) {
                            chatTextView.linksClickable = true
                            chatTextView.movementMethod = LinkMovementMethod.getInstance()
                        } else {
                            chatTextView.linksClickable = false
                        }

                        if (model.skillLocation.isNullOrEmpty()) {
                            thumbsUp?.visibility = View.GONE
                            thumbsDown?.visibility = View.GONE
                        } else {
                            thumbsUp?.visibility = View.VISIBLE
                            thumbsDown?.visibility = View.VISIBLE
                        }

                        if (model.isPositiveRated || model.isNegativeRated) {
                            thumbsUp?.visibility = View.GONE
                            thumbsDown?.visibility = View.GONE
                        } else {
                            thumbsUp?.setImageResource(R.drawable.thumbs_up_outline)
                            thumbsDown?.setImageResource(R.drawable.thumbs_down_outline)
                        }

                        chatTextView.text = answerText
                        timeStamp.text = model.timeStamp
                        chatTextView.tag = this
                        timeStamp.tag = this

                        thumbsUp?.setOnClickListener {
                            Timber.d("%s %s", model.isPositiveRated, model.isNegativeRated)
                            if (!model.isPositiveRated && !model.isNegativeRated) {
                                thumbsUp?.setImageResource(R.drawable.thumbs_up_solid)
                                model.skillLocation?.let { location -> rateSusiSkill(Constant.POSITIVE, location, context) }
                                setRating(true, true)
                            }
                        }

                        thumbsDown?.setOnClickListener {
                            Timber.d("%s %s", model.isPositiveRated, model.isNegativeRated)
                            if (!model.isPositiveRated && !model.isNegativeRated) {
                                thumbsDown?.setImageResource(R.drawable.thumbs_down_solid)
                                model.skillLocation?.let { location -> rateSusiSkill(Constant.NEGATIVE, location, context) }
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

    private fun setRating(what: Boolean, which: Boolean) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        if (which) {
            model?.isPositiveRated = what
        } else {
            model?.isNegativeRated = what
        }
        realm.commitTransaction()
    }

    private fun rateSusiSkill(polarity: String, locationUrl: String, context: Context) {
        val queryObject = ParseSusiResponseHelper.getSkillRatingQuery(locationUrl)?.copy(rating = polarity) ?: return

        val call = ClientBuilder.rateSkillCall(queryObject)

        call.enqueue(object : Callback<SkillRatingResponse> {
            override fun onResponse(call: Call<SkillRatingResponse>, response: Response<SkillRatingResponse>) {
                if (!response.isSuccessful || response.body() == null) {
                    when (polarity) {
                        Constant.POSITIVE -> if (thumbsUp != null) {
                            thumbsUp?.setImageResource(R.drawable.thumbs_up_outline)
                            setRating(false, true)
                        }
                        Constant.NEGATIVE -> if (thumbsDown != null) {
                            thumbsDown?.setImageResource(R.drawable.thumbs_down_outline)
                            setRating(false, false)
                        }
                    }
                    Toast.makeText(context, context.getString(R.string.error_rating), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SkillRatingResponse>, t: Throwable) {
                Timber.e(t)
                when (polarity) {
                    Constant.POSITIVE -> if (thumbsUp != null) {
                        thumbsUp?.setImageResource(R.drawable.thumbs_up_outline)
                        setRating(false, true)
                    }
                    Constant.NEGATIVE -> if (thumbsDown != null) {
                        thumbsDown?.setImageResource(R.drawable.thumbs_down_outline)
                        setRating(false, false)
                    }
                }
                Toast.makeText(context, context.getString(R.string.error_rating), Toast.LENGTH_SHORT).show()
            }
        })
    }
}