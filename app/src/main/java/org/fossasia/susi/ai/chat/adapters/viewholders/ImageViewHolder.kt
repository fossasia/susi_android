package org.fossasia.susi.ai.chat.adapters.viewholders

import android.content.Context
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import com.squareup.picasso.Picasso

import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.ParseSusiResponseHelper
import org.fossasia.susi.ai.data.model.ChatMessage
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.rest.ClientBuilder
import org.fossasia.susi.ai.rest.responses.susi.SkillRatingResponse

import butterknife.ButterKnife
import io.realm.Realm
import kotterknife.bindView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class ImageViewHolder(itemView: View, clickListener: MessageViewHolder.ClickListener) : MessageViewHolder(itemView, clickListener) {

    val imageView: ImageView by bindView(R.id.image_response)
    val timeStamp: TextView by bindView(R.id.timestamp)
    val thumbsUp: ImageView by bindView(R.id.thumbs_up)
    val thumbsDown: ImageView by bindView(R.id.thumbs_down)
    private var model: ChatMessage? = null
    private var imageURL: String? = null

    init {
        ButterKnife.bind(this, itemView)
    }

    fun setView(model: ChatMessage?) {
        this.model = model

        if (model != null) {
            imageURL = model.content
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

        if (model?.skillLocation.isNullOrEmpty()) {
            thumbsUp.visibility = View.GONE
            thumbsDown.visibility = View.GONE
        } else {
            thumbsUp.visibility = View.VISIBLE
            thumbsDown.visibility = View.VISIBLE
        }

        if (model != null && (model.isPositiveRated || model.isNegativeRated)) {
            thumbsUp.visibility = View.GONE
            thumbsDown.visibility = View.GONE
        } else {
            thumbsUp.setImageResource(R.drawable.thumbs_up_outline)
            thumbsDown.setImageResource(R.drawable.thumbs_down_outline)
        }

        timeStamp.text = model?.timeStamp

        thumbsUp.setOnClickListener {
            Timber.d("%s %s", model?.isPositiveRated, model?.isNegativeRated)
            if (model != null && !model.isPositiveRated && !model.isNegativeRated) {
                thumbsUp.setImageResource(R.drawable.thumbs_up_solid)
                model.skillLocation?.let { location -> rateSusiSkill(Constant.POSITIVE, location, itemView.context) }
                setRating(true, true)
            }
        }

        thumbsDown.setOnClickListener {
            Timber.d("%s %s", model?.isPositiveRated, model?.isNegativeRated)
            if (model != null && !model.isPositiveRated && !model.isNegativeRated) {
                thumbsDown.setImageResource(R.drawable.thumbs_down_solid)
                model.skillLocation?.let { location -> rateSusiSkill(Constant.NEGATIVE, location, itemView.context) }
                setRating(true, false)
            }
        }
    }

    // a function to rate the susi skill
    private fun rateSusiSkill(polarity: String, locationUrl: String, context: Context) {
        val queryObject = ParseSusiResponseHelper.getSkillRatingQuery(locationUrl)?.copy(rating = polarity) ?: return

        val ratingResponseCall = ClientBuilder.rateSkillCall(queryObject)

        ratingResponseCall.enqueue(object : Callback<SkillRatingResponse> {
            override fun onResponse(responseCall: Call<SkillRatingResponse>, response: Response<SkillRatingResponse>) {
                if (!response.isSuccessful || response.body() == null) {
                    updateRating(polarity)
                    Toast.makeText(context, context.getString(R.string.error_rating), Toast.LENGTH_SHORT).show()
                } else {
                    Timber.d("Rating successful")
                }
            }

            override fun onFailure(responseCall: Call<SkillRatingResponse>, t: Throwable) {
                Timber.e(t)
                updateRating(polarity)
                Toast.makeText(context, context.getString(R.string.error_rating), Toast.LENGTH_SHORT).show()
            }
        })
    }

    // function to set the rating in the database
    private fun setRating(rating: Boolean, thumbsUp: Boolean) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        if (thumbsUp) {
            model?.isPositiveRated = rating
        } else {
            model?.isNegativeRated = rating
        }
        realm.commitTransaction()
    }

    private fun updateRating(polarity: String) {
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
}
