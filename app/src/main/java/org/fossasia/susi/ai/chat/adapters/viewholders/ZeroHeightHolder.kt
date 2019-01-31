package org.fossasia.susi.ai.chat.adapters.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.RelativeLayout

import org.fossasia.susi.ai.R

import kotterknife.bindView

class ZeroHeightHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val chatMessage: RelativeLayout by bindView(R.id.chatMessageView)
}
