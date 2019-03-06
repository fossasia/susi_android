package org.fossasia.susi.ai.skills.skilllisting.adapters.recycleradapters

import android.content.Context
import android.support.annotation.NonNull
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.helper.Utils
import org.fossasia.susi.ai.rest.responses.susi.SkillData
import org.fossasia.susi.ai.skills.SkillFragmentCallback
import org.fossasia.susi.ai.skills.skilllisting.adapters.viewholders.SkillViewHolder
import timber.log.Timber

/**
 * adapter to show individual skill
 */
class SkillListAdapter(val context: Context, skillDetails: List<SkillData>?, val skillCallback: SkillFragmentCallback, val search: Boolean = false)
    : ListAdapter<SkillData, SkillViewHolder>(DIFF_CALLBACK),
        SkillViewHolder.ClickListener {
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SkillData>() {
            override fun areItemsTheSame(p0: SkillData, p1: SkillData): Boolean {
                return true
            }

            override fun areContentsTheSame(p0: SkillData, p1: SkillData): Boolean {
                return p0 == p1
            }
        }
    }

    private var list: List<SkillData>? = skillDetails

    /**
     * @param skillList updates the list variable
     */
    fun setSkillList(skillList: List<SkillData>) {
        list = skillList
    }

    private val clickListener: SkillViewHolder.ClickListener = this

    @NonNull
    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {
        val skillData: SkillData

        if (list != null) {

            skillData = getItem(position)

            if (TextUtils.isEmpty(skillData.skillName)) {
                holder.skillPreviewTitle.text = context.getString(R.string.no_skill_name)
            } else {
                holder.skillPreviewTitle.text = skillData.skillName
            }

            if (skillData.examples == null || skillData.examples.isEmpty()) {
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

    override fun onItemClicked(position: Int) {
        Timber.d("onItemClicked $position")
        val skillTag: String = getItem(position)?.skillTag ?: ""

        val skillData = list?.get(position)
        val skillGroup = list?.get(position)?.group?.replace(" ", "%20")
        showSkillDetailFragment(skillData, skillGroup, skillTag)
    }

    private fun showSkillDetailFragment(skillData: SkillData?, skillGroup: String?, skillTag: String) {
        skillCallback.loadDetailFragment(skillData, skillGroup, skillTag)
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
        val itemView: View = when (search) {
            true -> LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_skills, parent, false)
            else -> LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_skill, parent, false)
        }

        return SkillViewHolder(itemView, clickListener)
    }
}
