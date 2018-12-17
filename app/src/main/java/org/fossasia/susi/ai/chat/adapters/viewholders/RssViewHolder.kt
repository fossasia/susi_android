package org.fossasia.susi.ai.chat.adapters.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

import org.fossasia.susi.ai.R

import kotterknife.bindView

class RssViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val linkTextView: TextView by bindView(R.id.link)
    val titleTextView: TextView by bindView(R.id.title)
    val descriptionTextView: TextView by bindView(R.id.description)
    val parentLayout: LinearLayout by bindView(R.id.parent_layout)
}
