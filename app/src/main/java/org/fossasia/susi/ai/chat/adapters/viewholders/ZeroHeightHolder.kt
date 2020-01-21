package org.fossasia.susi.ai.chat.adapters.viewholders

import android.view.View
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import kotterknife.bindView
import org.fossasia.susi.ai.R

class ZeroHeightHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

    val chatMessage: RelativeLayout by bindView(R.id.chatMessageView)
}
