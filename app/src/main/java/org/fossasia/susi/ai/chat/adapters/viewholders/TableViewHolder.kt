package org.fossasia.susi.ai.chat.adapters.viewholders

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.ParseSusiResponseHelper
import org.fossasia.susi.ai.chat.adapters.recycleradapters.TableAdapter
import org.fossasia.susi.ai.data.model.ChatMessage
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.rest.ClientBuilder
import org.fossasia.susi.ai.rest.responses.susi.SkillRatingResponse

import java.util.ArrayList

import butterknife.ButterKnife
import io.realm.Realm
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

import kotterknife.bindOptionalView
import kotterknife.bindView

class TableViewHolder(itemView: View, clickListener: MessageViewHolder.ClickListener) : MessageViewHolder(itemView, clickListener) {

    val recyclerView: RecyclerView by bindView(R.id.recyclerView)
    val timeStamp: TextView by bindView(R.id.timestamp)
    val thumbsUp: ImageView? by bindOptionalView(R.id.thumbs_up)
    val thumbsDown: ImageView? by bindOptionalView(R.id.thumbs_down)
    private var model: ChatMessage? = null

    init {
        ButterKnife.bind(this, itemView)
    }

    fun setView(model: ChatMessage?) {
        this.model = model
        // check if model is not null else there is no need to set view elements
        if (model != null) {
            // check if the size of the data list and the column list is not 0
            val tableColumns = model.tableColumns
            val tableData = model.tableData
            if ((tableColumns != null && tableColumns.isNotEmpty()) || (tableData != null && tableData.isNotEmpty())) {
                val data = ArrayList<String?>()
                val column = ArrayList<String?>()

                if (tableColumns != null) {
                    for (col in tableColumns) {
                        column.add(col.columnName)
                    }
                }

                Timber.d("SIZE : %d : %d", column.size, data.size)

                if (tableData != null) {
                    for (tableDatum in tableData) {
                        data.add(tableDatum.tableData)
                    }
                }

                // Set the layout manager for the recyclerview and and call the TableAdapter to attach the recyclerview elements
                val layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL,
                        false)
                recyclerView.layoutManager = layoutManager
                val tableAdapter = TableAdapter(column, data)
                recyclerView.adapter = tableAdapter
            } else {
                recyclerView.layoutManager = null
                recyclerView.adapter = null
            }
        } else {
            recyclerView.layoutManager = null
            recyclerView.adapter = null
        }

        val skillLocation = model?.skillLocation
        if (model == null || (skillLocation != null && skillLocation.isEmpty())) {
            thumbsUp?.visibility = View.GONE
            thumbsDown?.visibility = View.GONE
        } else {
            thumbsUp?.visibility = View.VISIBLE
            thumbsDown?.visibility = View.VISIBLE
        }

        if (model != null && (model.isPositiveRated || model.isNegativeRated)) {
            thumbsUp?.visibility = View.GONE
            thumbsDown?.visibility = View.GONE
        } else {
            thumbsUp?.setImageResource(R.drawable.thumbs_up_outline)
            thumbsDown?.setImageResource(R.drawable.thumbs_down_outline)
        }

        timeStamp.text = model?.timeStamp
        timeStamp.tag = this

        thumbsUp?.setOnClickListener {
            Timber.d("%s %s", model?.isPositiveRated, model?.isNegativeRated)
            if (model != null && !model.isPositiveRated && !model.isNegativeRated && skillLocation != null) {
                thumbsUp?.setImageResource(R.drawable.thumbs_up_solid)
                rateSusiSkill(Constant.POSITIVE, skillLocation, itemView.context)
                setRating(true, true)
            }
        }

        thumbsDown?.setOnClickListener {
            Timber.d("%s %s", model?.isPositiveRated, model?.isNegativeRated)
            if (model != null && !model.isPositiveRated && !model.isNegativeRated && skillLocation != null) {
                thumbsDown?.setImageResource(R.drawable.thumbs_down_solid)
                rateSusiSkill(Constant.NEGATIVE, skillLocation, itemView.context)
                setRating(true, false)
            }
        }
    }

    // a function to rate the susi skill
    private fun rateSusiSkill(polarity: String, locationUrl: String, context: Context) {
        val queryObject = ParseSusiResponseHelper.getSkillRatingQuery(locationUrl)?.copy(rating = polarity) ?: return

        val call = ClientBuilder.rateSkillCall(queryObject)

        call.enqueue(object : Callback<SkillRatingResponse> {
            override fun onResponse(call: Call<SkillRatingResponse>, response: Response<SkillRatingResponse>) {
                if (!response.isSuccessful || response.body() == null) {
                    when (polarity) {
                        Constant.POSITIVE -> {
                            thumbsUp?.setImageResource(R.drawable.thumbs_up_outline)
                            setRating(false, true)
                        }
                        Constant.NEGATIVE -> {
                            thumbsDown?.setImageResource(R.drawable.thumbs_down_outline)
                            setRating(false, false)
                        }
                    }
                    Toast.makeText(context, context.getString(R.string.error_rating), Toast.LENGTH_SHORT).show()
                } else {
                    Timber.d("Response successful")
                }
            }

            override fun onFailure(call: Call<SkillRatingResponse>, t: Throwable) {
                Timber.d(t)
                when (polarity) {
                    Constant.POSITIVE -> {
                        thumbsUp?.setImageResource(R.drawable.thumbs_up_outline)
                        setRating(false, true)
                    }
                    Constant.NEGATIVE -> {
                        thumbsDown?.setImageResource(R.drawable.thumbs_down_outline)
                        setRating(false, false)
                    }
                }
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
}