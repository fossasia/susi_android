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
import org.fossasia.susi.ai.data.SkillDetailsModel
import org.fossasia.susi.ai.data.contract.ISkillDetailsModel
import org.fossasia.susi.ai.dataclasses.DeleteFeedbackQuery
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.PrefManager
import org.fossasia.susi.ai.helper.Utils
import org.fossasia.susi.ai.rest.responses.susi.GetSkillFeedbackResponse
import org.fossasia.susi.ai.rest.responses.susi.SkillData
import org.fossasia.susi.ai.skills.SkillsActivity
import org.fossasia.susi.ai.skills.feedback.FeedbackActivity
import org.fossasia.susi.ai.skills.skilldetails.adapters.viewholders.FeedbackViewHolder

/**
 *
 * Created by arundhati24 on 27/06/2018
 */
class FeedbackAdapter(
    val context: Context,
    private val feedbackResponse: GetSkillFeedbackResponse,
    val skillData: SkillData
) :
        RecyclerView.Adapter<FeedbackViewHolder>(), FeedbackViewHolder.ClickListener {

    private val clickListener: FeedbackViewHolder.ClickListener = this
    private val skillDetailsModel: ISkillDetailsModel = SkillDetailsModel()

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_feedback, parent, false)
        return FeedbackViewHolder(itemView, clickListener)
    }

    override fun getItemCount(): Int {
        if (feedbackResponse.feedbackList.isNotEmpty()) {
            if (feedbackResponse.feedbackList.size > 4) {
                return 4
            }
            return feedbackResponse.feedbackList.size
        }
        return 0
    }

    @NonNull
    override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
        if (feedbackResponse.feedbackList.isNotEmpty()) {
            if (feedbackResponse.feedbackList[position] != null) {
                if (position < 3) {
                    if (feedbackResponse.feedbackList[position].email != null &&
                            !TextUtils.isEmpty(feedbackResponse.feedbackList[position].email)) {
                        Utils.setAvatar(context, feedbackResponse.feedbackList.get(position).avatar, holder.avatar)
                        Utils.setUsername(feedbackResponse.feedbackList.get(position), holder.feedbackEmail)
                    }
                    if (feedbackResponse.feedbackList[position].timestamp != null &&
                            !TextUtils.isEmpty(feedbackResponse.feedbackList[position].timestamp)) {
                        val date: String? = getDate(feedbackResponse.feedbackList[position].timestamp)
                        if (date != null) {
                            holder.feedbackDate.text = date
                        } else {
                            holder.feedbackDate.text = ""
                        }
                    }
                    if (feedbackResponse.feedbackList[position].feedback != null &&
                            !TextUtils.isEmpty(feedbackResponse.feedbackList[position].feedback)) {
                        holder.feedback.text = feedbackResponse.feedbackList[position].feedback
                    }
                }
                if (feedbackResponse.feedbackList[position].email == PrefManager.getStringSet(Constant.SAVED_EMAIL)?.iterator()?.next()) {
                    holder.deleteFeedback.visibility = View.VISIBLE
                }
            }
            if (position == 3) {
                holder.itemFeedback.visibility = View.GONE
                holder.seeAllReviews.visibility = View.VISIBLE
            }
        }
    }

    private fun getDate(timestamp: String?): String? {
        var date: String? = ""
        timestamp?.trim()
        val month = timestamp?.substring(5, 7)?.toInt()
        if (month != null) {
            date = timestamp.substring(8, 10) + " " +
                    context.resources.getStringArray(R.array.months)[month - 1].toString() +
                    ", " + timestamp.substring(0, 4)
        }
        return date
    }

    override fun onItemClicked(position: Int) {
        if (context is Activity) context.overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out)
        val intent = Intent(context, FeedbackActivity::class.java)
        intent.putExtra("skillModel", skillData.model)
        intent.putExtra("skillGroup", skillData.group)
        intent.putExtra("skillLanguage", skillData.language)
        intent.putExtra("feedbackResponse", feedbackResponse)

        context.startActivity(intent)
    }

    override fun deleteClicked(position: Int) {
        val query: DeleteFeedbackQuery = DeleteFeedbackQuery(skillData.model, skillData.group, skillData.language, skillData.skillName, PrefManager.getString(Constant.ACCESS_TOKEN, ""))
        skillDetailsModel.deleteFeedback(query, context)
        if ((context is SkillsActivity)) {
            context.loadDetailFragment(skillData, skillData.group, skillData.skillTag)
        }
    }
}