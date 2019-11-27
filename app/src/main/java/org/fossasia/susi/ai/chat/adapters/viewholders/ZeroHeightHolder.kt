package org.fossasia.susi.ai.chat.adapters.viewholders

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.RelativeLayout
import kotterknife.bindView
import org.fossasia.susi.ai.R

class ZeroHeightHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

    val chatMessage: RelativeLayout by bindView(R.id.chatMessageView)
}
