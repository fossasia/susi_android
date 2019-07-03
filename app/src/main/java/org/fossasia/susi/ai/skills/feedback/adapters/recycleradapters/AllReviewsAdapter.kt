package org.fossasia.susi.ai.skills.feedback.adapters.recycleradapters

import android.content.Context
import android.support.annotation.NonNull
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.data.SkillDetailsModel
import org.fossasia.susi.ai.data.contract.ISkillDetailsModel
import org.fossasia.susi.ai.dataclasses.DeleteFeedbackQuery
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.PrefManager
import org.fossasia.susi.ai.helper.Utils
import org.fossasia.susi.ai.rest.responses.susi.Feedback
import org.fossasia.susi.ai.skills.feedback.FeedbackActivity.Companion.FEEDBACK_DELETED
import org.fossasia.susi.ai.skills.feedback.FeedbackActivity.Companion.FEEDBACK_DELETION_STATUS
import org.fossasia.susi.ai.skills.feedback.adapters.viewholders.AllReviewsViewHolder

/**
 *
 * Created by arundhati24 on 27/06/2018
 */
class AllReviewsAdapter(
    val context: Context,
    private val arrangedFeedbackList: ArrayList<Feedback>?,
    private val skillName: String,
    private val skillLanguage: String,
    private val skillGroup: String,
    private val skillModel: String
) :
        RecyclerView.Adapter<AllReviewsViewHolder>(), AllReviewsViewHolder.ClickListener {

    private val clickListener: AllReviewsViewHolder.ClickListener = this
    private val skillDetailsModel: ISkillDetailsModel = SkillDetailsModel()

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllReviewsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_feedback, parent, false)
        return AllReviewsViewHolder(itemView, clickListener)
    }

    override fun getItemCount(): Int {
        if (arrangedFeedbackList != null) {
            return arrangedFeedbackList.size
        }
        return 0
    }

    @NonNull
    override fun onBindViewHolder(holder: AllReviewsViewHolder, position: Int) {
        if (arrangedFeedbackList != null) {
            if (arrangedFeedbackList[position] != null) {
                if (arrangedFeedbackList[position].email != null &&
                        !TextUtils.isEmpty(arrangedFeedbackList[position].email)) {
                    Utils.setAvatar(context, arrangedFeedbackList.get(position).avatar, holder.avatar)
                    Utils.setUsername(arrangedFeedbackList.get(position), holder.feedbackEmail)
                }
                if (arrangedFeedbackList[position].timestamp != null &&
                        !TextUtils.isEmpty(arrangedFeedbackList[position].timestamp)) {
                    val date: String? = getDate(arrangedFeedbackList[position].timestamp)
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

                if (arrangedFeedbackList[position].email == PrefManager.getStringSet(Constant.SAVED_EMAIL)?.iterator()?.next()) {
                    holder.deleteFeedback.visibility = View.VISIBLE
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

    override fun deleteClicked(position: Int) {
        val query: DeleteFeedbackQuery = DeleteFeedbackQuery(skillModel, skillGroup, skillLanguage, skillName, PrefManager.getString(Constant.ACCESS_TOKEN, ""))
        skillDetailsModel.deleteFeedback(query, context)
        FEEDBACK_DELETION_STATUS = FEEDBACK_DELETED
        arrangedFeedbackList?.removeAt(position)
        notifyItemChanged(position)
        notifyItemRangeRemoved(position, 1)
    }
}
