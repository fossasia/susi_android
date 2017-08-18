package org.fossasia.susi.ai.skills.skillListing.adapters.recyclerAdapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
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
class SkillListAdapter(val context: Context, val skillDetails:  Pair<String, Map<String, SkillData>>) : RecyclerView.Adapter<SkillViewHolder>() {

    val imageLink = "https://raw.githubusercontent.com/fossasia/susi_skill_data/master/models/general/"

    override fun onBindViewHolder(holder: SkillViewHolder?, position: Int) {
        val skillData = skillDetails.second.values.toTypedArray()[position]
        Log.v("chirag","skillListAdapter : " + skillData.skillName + " " + skillData.descriptions + " ")
        if(skillData.skillName == null || skillData.skillName.isEmpty()){
            holder?.skillPreviewTitle?.text = context.getString(R.string.no_skill_name)
        } else {
            holder?.skillPreviewTitle?.text = skillData.skillName
        }
        if( skillData.descriptions == null || skillData.descriptions.isEmpty()){
            holder?.skillPreviewDescription?.text = context.getString(R.string.no_skill_description)
        } else {
            holder?.skillPreviewDescription?.text = skillData.descriptions
        }
        holder?.skillPreviewExample?.text = StringBuilder("\"").append(skillData.examples[0]).append("\"")
        if(skillData.image == null || skillData.image.isEmpty()){
            holder?.previewImageView?.setImageResource(R.drawable.ic_susi)
        } else {
            Picasso.with(context.applicationContext).load(StringBuilder(imageLink)
                    .append(skillDetails.first).append("/en/").append(skillData.image).toString())
                    .fit().centerCrop()
                    .into(holder?.previewImageView)
        }
    }

    override fun getItemCount(): Int {
        return skillDetails.second.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SkillViewHolder {
        val itemView = LayoutInflater.from(parent?.context)
                .inflate(R.layout.item_skill, parent, false)
        return SkillViewHolder(itemView)
    }
}