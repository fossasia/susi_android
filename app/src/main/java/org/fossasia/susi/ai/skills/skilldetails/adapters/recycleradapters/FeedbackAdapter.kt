package org.fossasia.susi.ai.skills.skilldetails.adapters.recycleradapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.annotation.NonNull
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.DateTimeHelper
import org.fossasia.susi.ai.helper.PrefManager
import org.fossasia.susi.ai.helper.Utils
import org.fossasia.susi.ai.rest.responses.susi.Feedback
import org.fossasia.susi.ai.rest.responses.susi.GetSkillFeedbackResponse
import org.fossasia.susi.ai.skills.feedback.FeedbackActivity
import org.fossasia.susi.ai.skills.skilldetails.adapters.viewholders.FeedbackViewHolder
import timber.log.Timber

/**
 *
 * Created by arundhati24 on 27/06/2018
 */
class FeedbackAdapter(
    val context: Context,
    private val feedbackResponse: GetSkillFeedbackResponse
) :
        RecyclerView.Adapter<FeedbackViewHolder>(), FeedbackViewHolder.ClickListener {

    private val clickListener: FeedbackViewHolder.ClickListener = this
    private val arrangedFeedbackList: ArrayList<Feedback> = ArrayList()

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_feedback, parent, false)
        return FeedbackViewHolder(itemView, clickListener)
    }

    override fun getItemCount(): Int {
        arrangeFeedbacks()
        if (feedbackResponse.feedbackList.isNotEmpty()) {
            if (feedbackResponse.feedbackList.size > 4) {
                return 4
            }
            return feedbackResponse.feedbackList.size
        }
        return 0
    }

    private fun arrangeFeedbacks() {
        arrangedFeedbackList.clear()
        feedbackResponse.feedbackList.forEach { item ->
            if (item.email == PrefManager.getStringSet(Constant.SAVED_EMAIL)?.iterator()?.next()) {
                arrangedFeedbackList.add(item)
            }
        }

        val reverseResponse = feedbackResponse.feedbackList.asReversed()
        reverseResponse.forEach { item ->
            if (item.email != PrefManager.getStringSet(Constant.SAVED_EMAIL)?.iterator()?.next()) {
                arrangedFeedbackList.add(item)
            }
        }
        Timber.d("Arranged the feedback")
    }

    @NonNull
    override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
        if (arrangedFeedbackList.isNotEmpty()) {
            if (arrangedFeedbackList[position] != null) {
                if (position < 3) {
                    if (arrangedFeedbackList[position].email != null &&
                            !TextUtils.isEmpty(arrangedFeedbackList[position].email)) {
                        Utils.setAvatar(context, arrangedFeedbackList.get(position).avatar, holder.avatar)
                        Utils.setUsername(arrangedFeedbackList.get(position), holder.feedbackEmail)
                    }
                    if (arrangedFeedbackList[position].timestamp != null &&
                            !TextUtils.isEmpty(arrangedFeedbackList[position].timestamp)) {
                        val date: String? = DateTimeHelper.formatDate(arrangedFeedbackList[position].timestamp as String, context.resources.getStringArray(R.array.months))
                        if (date != null) {
                            holder.feedbackDate.text = date
                        } else {
                            holder.feedbackDate.text = ""
                        }
                    }
                    if (arrangedFeedbackList[position].feedback != null &&
                            !TextUtils.isEmpty(arrangedFeedbackList[position].feedback)) {
                        holder.feedback.text = arrangedFeedbackList[position].feedback
                    }
                }
            }
            if (position == 3) {
                holder.itemFeedback.visibility = View.GONE
                holder.seeAllReviews.visibility = View.VISIBLE
            }
        }
    }

    override fun onItemClicked(position: Int) {
        if (context is Activity) context.overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out)
        val intent = Intent(context, FeedbackActivity::class.java)
        intent.putExtra("feedbackResponse", feedbackResponse)
        intent.putExtra("arrangedFeedbackList", arrangedFeedbackList)
        context.startActivity(intent)
    }
}
