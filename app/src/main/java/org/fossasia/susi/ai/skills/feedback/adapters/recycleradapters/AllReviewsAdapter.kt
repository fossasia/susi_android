package org.fossasia.susi.ai.skills.feedback.adapters.recycleradapters

import android.content.Context
import android.support.annotation.NonNull
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.helper.Utils
import org.fossasia.susi.ai.rest.responses.susi.Feedback
import org.fossasia.susi.ai.skills.feedback.adapters.viewholders.AllReviewsViewHolder

/**
 *
 * Created by arundhati24 on 27/06/2018
 */
class AllReviewsAdapter(val context: Context, val feedbackList: List<Feedback>?) :
        RecyclerView.Adapter<AllReviewsViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllReviewsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_feedback, parent, false)
        return AllReviewsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        if (feedbackList != null) {
            return feedbackList.size
        }
        return 0
    }

    @NonNull
    override fun onBindViewHolder(holder: AllReviewsViewHolder, position: Int) {
        if (feedbackList != null) {
            if (feedbackList[position] != null) {
                if (feedbackList[position].email != null &&
                        !TextUtils.isEmpty(feedbackList[position].email)) {
                    Utils.setAvatar(context, feedbackList.get(position).avatar, holder.avatar)
                    Utils.setUsername(feedbackList.get(position), holder.feedbackEmail)
                }
                if (feedbackList[position].timestamp != null &&
                        !TextUtils.isEmpty(feedbackList[position].timestamp)) {
                    val date: String? = getDate(feedbackList[position].timestamp)
                    if (date != null) {
                        holder.feedbackDate.text = date
                    } else {
                        holder.feedbackDate.text = ""
                    }
                }
                if (feedbackList[position].feedback != null &&
                        !TextUtils.isEmpty(feedbackList[position].feedback)) {
                    holder.feedback.text = feedbackList[position].feedback
                }
            }
        }
    }

    private fun getDate(timestamp: String?): String? {
        var date: String? = ""
        if (timestamp != null && !TextUtils.isEmpty(timestamp)) {
            timestamp.trim()
            val month = timestamp.substring(5, 7).toInt()
            date = timestamp.substring(8, 10) + " " +
                    context.resources.getStringArray(R.array.months)[month - 1].toString() +
                    ", " + timestamp.substring(0, 4)
        }
        return date
    }
}
