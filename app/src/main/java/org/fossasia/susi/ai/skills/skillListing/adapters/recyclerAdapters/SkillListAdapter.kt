package org.fossasia.susi.ai.skills.skillListing.adapters.recyclerAdapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.rest.responses.susi.SkillData
import org.fossasia.susi.ai.skills.skillListing.adapters.viewHolders.SkillViewHolder

/**
 *
 * Created by chiragw15 on 15/8/17.
 */
class SkillListAdapter(val context: Context, val skillDetails: Map<String, SkillData>)
    : RecyclerView.Adapter<SkillViewHolder>() {

    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {
        holder.skillPreviewTitle?.text = skillDetails.values.toTypedArray()[position].skillName
        holder.skillPreviewDescription?.text = skillDetails.values.toTypedArray()[position].descriptions
        holder.skillPreviewExample?.text = skillDetails.values.toTypedArray()[position].examples[0]
        Picasso.with(context.applicationContext).load(skillDetails.values.toTypedArray()[position].image)
                .fit().centerCrop()
                .into(holder.previewImageView)
    }

    override fun getItemCount(): Int {
        return skillDetails.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SkillViewHolder {
        val itemView = LayoutInflater.from(parent?.context)
                .inflate(R.layout.item_skill, parent, false)
        return SkillViewHolder(itemView)
    }
}