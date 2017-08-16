package org.fossasia.susi.ai.skills.skillListing.adapters.viewHolders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import org.fossasia.susi.ai.R

/**
 *
 * Created by chiragw15 on 16/8/17.
 */
class SkillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    @BindView(R.id.skill_preview_image)
    var previewImageView: ImageView? = null
    @BindView(R.id.skill_preview_description)
    var skillPreviewDescription: TextView? = null
    @BindView(R.id.skill_preview_title)
    var skillPreviewTitle: TextView ?= null
    @BindView(R.id.skill_preview_example)
    var skillPreviewExample: TextView ?= null

    init {
        ButterKnife.bind(this, itemView)
    }
}