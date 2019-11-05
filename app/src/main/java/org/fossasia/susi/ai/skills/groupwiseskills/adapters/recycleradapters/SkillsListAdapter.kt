package org.fossasia.susi.ai.skills.groupwiseskills.adapters.recycleradapters

import android.content.Context
import android.support.annotation.NonNull
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.dataclasses.GroupWiseSkills
import org.fossasia.susi.ai.helper.CircleTransform
import org.fossasia.susi.ai.rest.responses.susi.SkillData
import org.fossasia.susi.ai.skills.SkillFragmentCallback
import org.fossasia.susi.ai.skills.groupwiseskills.adapters.viewholders.SkillViewHolder

/**
 *
 * Created by arundhati24 on 16/07/2018.
 */
class SkillsListAdapter(
    val context: Context,
    private val skillDetails: GroupWiseSkills,
    private val skillCallback: SkillFragmentCallback
) :
        RecyclerView.Adapter<SkillViewHolder>(), SkillViewHolder.ClickListener {

    private val DEFAULT_AUTHOR = "<author_name>"
    private val DEFAULT_EXAMPLE = "<The question that should be shown in public skill displays>"
    private val imageLink = "https://raw.githubusercontent.com/fossasia/susi_skill_data/master/models/general/"
    private val clickListener: SkillViewHolder.ClickListener = this

    @NonNull
    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {
        val skillData = skillDetails.skillsList.get(position)

        if (skillData.skillName != null) {
            if (skillData.skillName.isEmpty()) {
                holder.skillName.text = context.getString(R.string.no_skill_name)
            } else {
                holder.skillName.text = skillData.skillName
            }
        }

        if (skillData.author != null) {
            if (skillData.author.isEmpty() || skillData.author.equals(DEFAULT_AUTHOR)) {
                holder.skillAuthorName.text = context.getString(R.string.no_skill_author)
            } else {
                holder.skillAuthorName.text = skillData.author
            }
        }

        if (skillData.examples != null) {
            if (skillData.examples.isEmpty() || skillData.examples.contains(DEFAULT_EXAMPLE))
                holder.skillExample.text = ""
            else
                holder.skillExample.text = StringBuilder("\"").append(skillData.examples[0]).append("\"")
        }

        if (skillData.image != null) {
            if (skillData.image.isEmpty()) {
                holder.skillImage.setImageResource(R.drawable.ic_susi)
            } else {
                val imageUrl: String = skillDetails.skillsList.get(position).group.replace(" ", "%20") + "/en/" + skillData.image
                Picasso.get().load(StringBuilder(imageLink)
                        .append(imageUrl).toString())
                        .fit().centerCrop()
                        .error(R.drawable.ic_susi)
                        .transform(CircleTransform())
                        .into(holder.skillImage)
            }
        }
        val stars = skillData.skillRating?.stars
        if (stars != null) {
            holder.skillRating.rating = stars.averageStar
            holder.skillTotalRatings.text = stars.totalStar.toString()
        }
    }

    override fun getItemCount(): Int {
        return skillDetails.skillsList.size
    }

    override fun onItemClicked(position: Int) {
        var skillTag: String? = ""
        if (skillDetails.skillsList[position].skillTag != null) {
            skillTag = skillDetails.skillsList.toTypedArray().get(position).skillTag
        } else {
            skillTag = ""
        }
        val skillData = skillDetails.skillsList.get(position)
        val skillGroup = skillDetails.skillsList.get(position).group.replace(" ", "%20")
        showSkillDetailFragment(skillData, skillGroup, skillTag)
    }

    private fun showSkillDetailFragment(skillData: SkillData, skillGroup: String, skillTag: String) {
        skillCallback.loadDetailFragment(skillData, skillGroup, skillTag)
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_group_wise_skill, parent, false)
        return SkillViewHolder(itemView, clickListener)
    }
}
