package org.fossasia.susi.ai.chat.adapters.viewholders

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import io.realm.Realm
import kotterknife.bindView
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.ParseSusiResponseHelper
import org.fossasia.susi.ai.data.model.ChatMessage
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.rest.ClientBuilder
import org.fossasia.susi.ai.rest.responses.susi.SkillRatingResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

abstract class MessageViewHolder(itemView: View, private val listener: ClickListener?) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {

    init {
        itemView.setOnClickListener(this)
        itemView.setOnLongClickListener(this)
    }

    val thumbsUp: ImageView by bindView(R.id.thumbs_up)
    val thumbsDown: ImageView by bindView(R.id.thumbs_down)
    val resendMessage: ImageView by bindView(R.id.resend_message)
    var model: ChatMessage? = null

    override fun onClick(view: View) {
        listener?.onItemClicked(adapterPosition)
    }

    override fun onLongClick(view: View): Boolean {
        return listener != null && listener.onItemLongClicked(adapterPosition)
    }

    /**
     * The interface Click listener.
     */
    interface ClickListener {
        /**
         * On item clicked.
         *
         * @param position the position
         */
        fun onItemClicked(position: Int)

        /**
         * On item long clicked boolean.
         *
         * @param position the position
         * @return the boolean
         */
        fun onItemLongClicked(position: Int): Boolean
    }

    fun setRating(rating: Boolean, thumbsUp: Boolean) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        if (thumbsUp) {
            model?.isPositiveRated = rating
        } else {
            model?.isNegativeRated = rating
        }
        realm.commitTransaction()
    }

    fun updateRating(polarity: String) {
        when (polarity) {
            Constant.POSITIVE -> {
                thumbsUp.setImageResource(R.drawable.thumbs_up_outline)
                setRating(false, true)
            }
            Constant.NEGATIVE -> {
                thumbsDown.setImageResource(R.drawable.thumbs_down_outline)
                setRating(false, false)
            }
        }
    }

    fun rateSusiSkill(polarity: String, locationUrl: String, context: Context) {
        val queryObject = ParseSusiResponseHelper.getSkillRatingQuery(locationUrl)?.copy(rating = polarity)
                ?: return

        val ratingResponseCall = ClientBuilder.rateSkillCall(queryObject)

        ratingResponseCall.enqueue(object : Callback<SkillRatingResponse> {
            override fun onResponse(responseCall: Call<SkillRatingResponse>, response: Response<SkillRatingResponse>) {
                if (!response.isSuccessful || response.body() == null) {
                    updateRating(polarity)
                    Toast.makeText(context, context.getString(R.string.error_rating), Toast.LENGTH_SHORT).show()
                } else {
                    Timber.d("Rating successful")
                    Toast.makeText(context, R.string.rate_chat, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(responseCall: Call<SkillRatingResponse>, t: Throwable) {
                Timber.e(t)
                updateRating(polarity)
                Toast.makeText(context, context.getString(R.string.error_rating), Toast.LENGTH_SHORT).show()
            }
        })
    }
}
