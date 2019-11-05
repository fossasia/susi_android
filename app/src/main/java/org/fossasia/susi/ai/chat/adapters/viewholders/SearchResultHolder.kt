package org.fossasia.susi.ai.chat.adapters.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import kotterknife.bindView
import org.fossasia.susi.ai.R

class SearchResultHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val previewImageView: ImageView by bindView(R.id.link_preview_image)
    val titleTextView: TextView by bindView(R.id.link_preview_title)
    val descriptionTextView: TextView by bindView(R.id.link_preview_description)
    val previewLayout: LinearLayout by bindView(R.id.preview_layout)
    val previewTextLayout: LinearLayout by bindView(R.id.link_preview_text_layout)
}
