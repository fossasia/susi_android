package org.fossasia.susi.ai.chat.adapters.viewholders

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import butterknife.ButterKnife
import java.util.ArrayList
import kotterknife.bindView
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.adapters.recycleradapters.TableAdapter
import org.fossasia.susi.ai.data.model.ChatMessage
import org.fossasia.susi.ai.helper.Constant
import timber.log.Timber

/**
 * ViewHolder for drawing table item layout.
 */
class TableViewHolder(itemView: View, clickListener: ClickListener) : MessageViewHolder(itemView, clickListener) {

    val recyclerView: RecyclerView by bindView(R.id.recyclerView)
    val timeStamp: TextView by bindView(R.id.timestamp)

    init {
        ButterKnife.bind(this, itemView)
    }

    fun setView(chatModel: ChatMessage?) {
        model = chatModel
        // check if model is not null else there is no need to set view elements
        if (chatModel != null) {
            // check if the size of the data list and the column list is not 0
            val tableColumns = chatModel.tableColumns
            val tableData = chatModel.tableData
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

        if (chatModel == null || TextUtils.isEmpty(chatModel.skillLocation)) {
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

        timeStamp.text = model?.timeStamp
        timeStamp.tag = this

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
