package org.fossasia.susi.ai.chat.adapters.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView

import org.fossasia.susi.ai.R

import kotterknife.bindView

class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val textDate: TextView by bindView(R.id.date)
}
