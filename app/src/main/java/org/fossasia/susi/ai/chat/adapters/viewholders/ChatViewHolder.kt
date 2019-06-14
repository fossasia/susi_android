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
import io.realm.Realm
import kotterknife.bindOptionalView
import kotterknife.bindView
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.ParseSusiResponseHelper
import org.fossasia.susi.ai.chat.adapters.recycleradapters.ChatFeedRecyclerAdapter
import org.fossasia.susi.ai.data.model.ChatMessage
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.rest.ClientBuilder
import org.fossasia.susi.ai.rest.responses.susi.SkillRatingResponse
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeFormatterBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

/**
 * ViewHolder for drawing chat item layout.
 */
class ChatViewHolder(view: View, clickListener: MessageViewHolder.ClickListener, myMessage: Int) :
        MessageViewHolder(view, clickListener) {

    private val chatTextView: TextView by bindView(R.id.text)
    private val receivedTick: ImageView? by bindOptionalView(R.id.received_tick)
    val timeStamp: TextView by bindView(R.id.timestamp)
    val backgroundLayout: LinearLayout by bindView(R.id.background_layout)
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

                        if (answerText.toString().contains("CEST")) {
                            val dateTime = getDateTime(answerText.toString())
                            chatTextView.text = dateTime.toString()
                        } else {
                            chatTextView.text = answerText
                        }
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

    private fun getDateTime(answer: String): Any {
        // Extracting the datas in required format
        val data = answer.split(":")
        var temp_hour = data.get(0).trim()
        var am_pm = data.get(2).subSequence(3, 5).trim().toString().toUpperCase()
        var hour = getHour(temp_hour, am_pm).toString()

        if (hour.length == 1) {
            hour = "0" + hour
        }
        val min = data.get(1).trim()
        val sec = data.get(2).subSequence(0, 2).toString()
        val dateData = answer.split(",")
        val date = dateData.get(1).split(" ").get(2).trim()
        val temp_month = dateData.get(1).split(" ").get(1).trim()
        val month = getMonth(temp_month.toLowerCase())
        val year = dateData.get(2).trim()

        // Formaulating the date into desirable form
        val formatted_date = year + "-" + month + "-" + date + "T" + hour + ":" + min + ":" + sec + " CET"

        val formatter = DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ISO_DATE_TIME)
                .optionalStart()
                .appendLiteral(' ')
                .parseCaseSensitive()
                .appendZoneRegionId()
                .toFormatter()

        var cest_format_date = ZonedDateTime.parse(formatted_date, formatter)
        var epochSeconds = cest_format_date.toEpochSecond()
        val local_date_format = ZonedDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneId.systemDefault())

        // Setting the display ready
        var display = local_date_format.hour.toString() + ":" + local_date_format.minute.toString() + ":"
        display += local_date_format.second.toString() + " | " + local_date_format.dayOfWeek.toString() + ", "
        display += local_date_format.dayOfMonth.toString() + " " + local_date_format.month.toString() + " " + local_date_format.year.toString()

        return display
    }

    private fun getHour(temp_hour: String, am_pm: String): Any? {
        var hour = ""
        if (am_pm == "PM") {
            hour = (12 + temp_hour.toInt()).toString()
        } else {
            hour = temp_hour
        }
        return hour
    }

    private fun getMonth(temp_month: String): Any? {
        if (temp_month.contains("jan")) {
            return "01"
        } else if (temp_month.contains("feb")) {
            return "02"
        } else if (temp_month.contains("mar")) {
            return "03"
        } else if (temp_month.contains("apr")) {
            return "04"
        } else if (temp_month.contains("may")) {
            return "05"
        } else if (temp_month.contains("jun")) {
            return "06"
        } else if (temp_month.contains("jul")) {
            return "07"
        } else if (temp_month.contains("aug")) {
            return "08"
        } else if (temp_month.contains("sep")) {
            return "09"
        } else if (temp_month.contains("oct")) {
            return "10"
        } else if (temp_month.contains("nov")) {
            return "11"
        } else if (temp_month.contains("dec")) {
            return "12"
        }
        return ""
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
        val queryObject = ParseSusiResponseHelper.getSkillRatingQuery(locationUrl)?.copy(rating = polarity)
                ?: return

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
                } else {
                    Toast.makeText(context, R.string.rate_chat, Toast.LENGTH_SHORT).show()
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