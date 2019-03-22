package org.fossasia.susi.ai.skills.skilllisting.adapters.recycleradapters

import android.content.Context
import android.support.annotation.NonNull
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.helper.Utils
import org.fossasia.susi.ai.rest.responses.susi.SkillData
import org.fossasia.susi.ai.skills.SkillFragmentCallback
import org.fossasia.susi.ai.skills.skilllisting.adapters.viewholders.SkillViewHolder

class SkillListAdapter(val context: Context, private val skillDetails: List<SkillData>?, val skillCallback: SkillFragmentCallback) : RecyclerView.Adapter<SkillViewHolder>(),
        SkillViewHolder.ClickListener {

    private val clickListener: SkillViewHolder.ClickListener = this

    @NonNull
    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {
        if (skillDetails != null) {
            val skillData = skillDetails.toTypedArray()[position]

            if (TextUtils.isEmpty(skillData.skillName)) {
                holder.skillPreviewTitle.text = context.getString(R.string.no_skill_name)
            } else {
                holder.skillPreviewTitle.text = skillData.skillName
            }

            if (skillData.examples.isNullOrEmpty()) {
                holder.skillPreviewExample.text = ""
            } else {
                holder.skillPreviewExample.text = StringBuilder("\"").append(skillData.examples[0]).append("\"")
            }

            if (TextUtils.isEmpty(skillData.image)) {
                holder.previewImageView.setImageResource(R.drawable.ic_susi)
            } else {
                Utils.setSkillsImage(skillData, holder.previewImageView)
            }

            val stars = skillData.skillRating?.stars
            if (stars != null) {
                holder.skillRatingBar.rating = stars.averageStar
                holder.totalRatings.text = stars.totalStar.toString()
            }
        }
    }

    override fun getItemCount(): Int {
        return (skillDetails as List<SkillData>).size
    }

    override fun onItemClicked(position: Int) {
        val skillTag = skillDetails?.toTypedArray()?.get(position)?.skillTag ?: ""
        val skillData = skillDetails?.get(position)
        val skillGroup = skillDetails?.get(position)?.group?.replace(" ", "%20")
        showSkillDetailFragment(skillData, skillGroup, skillTag)
    }

    private fun showSkillDetailFragment(skillData: SkillData?, skillGroup: String?, skillTag: String) {
        skillCallback.loadDetailFragment(skillData, skillGroup, skillTag)
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_skill, parent, false)
        return SkillViewHolder(itemView, clickListener)
    }
}
