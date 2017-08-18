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
        holder?.skillPreviewTitle?.text = skillData.skillName
        holder?.skillPreviewDescription?.text = skillData.descriptions
        holder?.skillPreviewExample?.text = skillData.examples[0]
        Picasso.with(context.applicationContext).load(imageLink+skillDetails.first+"/en/"+skillData.image)
                .fit().centerCrop()
                .into(holder?.previewImageView)
    }

    override fun getItemCount(): Int {
        Log.v("chirag","skill list item count : " + skillDetails.second.size)
        return skillDetails.second.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SkillViewHolder {
        Log.v("chirag","skill view getting created here")
        val itemView = LayoutInflater.from(parent?.context)
                .inflate(R.layout.item_skill, parent, false)
        return SkillViewHolder(itemView)
    }
}