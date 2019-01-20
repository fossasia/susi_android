package org.fossasia.susi.ai.chat.adapters.viewholders

import android.content.Context
import android.net.Uri
import android.os.Build
import android.support.customtabs.CustomTabsIntent
import android.text.Html
import android.text.Spanned
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.squareup.picasso.Picasso

import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.ParseSusiResponseHelper
import org.fossasia.susi.ai.chat.adapters.recycleradapters.ChatFeedRecyclerAdapter
import org.fossasia.susi.ai.data.model.ChatMessage
import org.fossasia.susi.ai.data.model.WebLink
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.PrefManager
import org.fossasia.susi.ai.rest.ClientBuilder
import org.fossasia.susi.ai.rest.responses.susi.SkillRatingResponse

import butterknife.ButterKnife
import io.github.ponnamkarthik.richlinkpreview.MetaData
import io.github.ponnamkarthik.richlinkpreview.ResponseListener
import io.github.ponnamkarthik.richlinkpreview.RichPreview
import io.realm.Realm
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

import kotterknife.bindView

class LinkPreviewViewHolder(itemView: View, listener: MessageViewHolder.ClickListener) : MessageViewHolder(itemView, listener) {

    val text: TextView by bindView(R.id.text)
    val backgroundLayout: LinearLayout by bindView(R.id.background_layout)
    val previewImageView: ImageView by bindView(R.id.link_preview_image)
    val titleTextView: TextView by bindView(R.id.link_preview_title)
    val descriptionTextView: TextView by bindView(R.id.link_preview_description)
    val timestampTextView: TextView by bindView(R.id.timestamp)
    val previewLayout: LinearLayout by bindView(R.id.preview_layout)
    val receivedTick: ImageView? by bindView(R.id.received_tick)
    val thumbsUp: ImageView? by bindView(R.id.thumbs_up)
    val thumbsDown: ImageView? by bindView(R.id.thumbs_down)

    private val realm: Realm = Realm.getDefaultInstance()
    private var url: String? = null
    private var model: ChatMessage? = null

    private val responseListener: ResponseListener by lazy {
        object : ResponseListener {
            override fun onData(data: MetaData?) {
                if (!PrefManager.hasTokenExpired() || PrefManager.getBoolean(R.string.anonymous_logged_in_key, false)) {
                    realm.beginTransaction()
                    val realm = Realm.getDefaultInstance()
                    val link = realm.createObject(WebLink::class.java)

                    if (data != null) {

                        if (!TextUtils.isEmpty(data.description)) {
                            Timber.d("onPos: %s", data.description)
                            previewLayout.visibility = View.VISIBLE
                            descriptionTextView.visibility = View.VISIBLE
                            descriptionTextView.text = data.description
                        }

                        if (!TextUtils.isEmpty(data.title)) {
                            Timber.d("onPos: %s", data.title)
                            previewLayout.visibility = View.VISIBLE
                            titleTextView.visibility = View.VISIBLE
                            titleTextView.text = data.title
                        }

                        link.body = data.description
                        link.headline = data.title
                        link.url = data.url
                        url = data.url

                        val imageLink = data.imageurl

                        if (TextUtils.isEmpty(imageLink)) {
                            previewImageView.visibility = View.GONE
                            link.imageURL = ""
                        } else {
                            previewImageView.visibility = View.VISIBLE
                            Picasso.get()
                                    .load(imageLink)
                                    .fit()
                                    .centerCrop()
                                    .into(previewImageView)
                            link.imageURL = imageLink
                        }
                    }

                    model?.webLinkData = link
                    model?.let {
                        realm.copyToRealmOrUpdate(it)
                    }
                    realm.commitTransaction()
                }
            }

            override fun onError(e: Exception) {
                Timber.e(e)
            }
        }
    }

    init {
        ButterKnife.bind(this, itemView)
    }

    /**
     * Inflate Link Preview
     *
     * @param model the ChatMessage object
     * @param currContext the Context
     */
    fun setView(model: ChatMessage, viewType: Int, currContext: Context) {
        this.model = model
        val answerText: Spanned = if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(model.content, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(model.content)
        }
        text.linksClickable = true
        text.movementMethod = LinkMovementMethod.getInstance()

        if (viewType == ChatFeedRecyclerAdapter.USER_WITHLINK) {
            if (model.isDelivered) {
                receivedTick?.setImageResource(R.drawable.ic_check)
            } else {
                receivedTick?.setImageResource(R.drawable.ic_clock)
            }
        }

        if (viewType != ChatFeedRecyclerAdapter.USER_WITHLINK) {
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

            thumbsUp?.setOnClickListener {
                val skillLocation = model.skillLocation
                if (!model.isPositiveRated && !model.isNegativeRated && skillLocation != null) {
                    thumbsUp?.setImageResource(R.drawable.thumbs_up_solid)
                    rateSusiSkill(Constant.POSITIVE, skillLocation, currContext)
                    setRating(true, true)
                }
            }

            thumbsDown?.setOnClickListener {
                val skillLocation = model.skillLocation
                if (!model.isPositiveRated && !model.isNegativeRated && skillLocation != null) {
                    thumbsDown?.setImageResource(R.drawable.thumbs_down_solid)
                    rateSusiSkill(Constant.NEGATIVE, skillLocation, currContext)
                    setRating(true, false)
                }
            }
        }

        text.text = answerText
        timestampTextView.text = model.timeStamp
        val webLinkData = model.webLinkData
        if (webLinkData == null) {
            previewImageView.visibility = View.GONE
            descriptionTextView.visibility = View.GONE
            titleTextView.visibility = View.GONE
            previewLayout.visibility = View.GONE

            val richPreview = RichPreview(responseListener)

            val urlList = ChatFeedRecyclerAdapter.extractLinks(model.content!!)
            var url = urlList[0]
            val http = "http://"
            val https = "https://"
            if (!(url.startsWith(http) || url.startsWith(https))) {
                url = https + url
            }

            richPreview.getPreview(url)
        } else {

            if (!webLinkData.headline.isNullOrEmpty()) {
                Timber.d("onPos: %s", webLinkData.headline)
                titleTextView.text = webLinkData.headline
            } else {
                titleTextView.visibility = View.GONE
                Timber.d("handleItemEvents: isEmpty")
            }

            if (!webLinkData.body.isNullOrEmpty()) {
                Timber.d("onPos: %s", webLinkData.headline)
                descriptionTextView.text = webLinkData.body
            } else {
                descriptionTextView.visibility = View.GONE
                Timber.d("handleItemEvents: isEmpty")
            }

            if (webLinkData.headline.isNullOrEmpty() && webLinkData.body.isNullOrEmpty()) {
                previewLayout.visibility = View.GONE
            }

            Timber.i(webLinkData.imageURL)
            if (!webLinkData.imageURL.isNullOrEmpty()) {
                Picasso.get()
                        .load(webLinkData.imageURL)
                        .fit().centerCrop()
                        .into(previewImageView)
            } else {
                previewImageView.visibility = View.GONE
            }

            url = webLinkData.url
        }

        /*
          Redirects to the link through chrome custom tabs
         */

        previewLayout.setOnClickListener {
            val webpage = Uri.parse(url)
            val builder = CustomTabsIntent.Builder() //custom tabs intent builder
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(currContext, webpage) //launching through custom tabs
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
