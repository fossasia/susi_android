package org.fossasia.susi.ai.chat.search.adapters

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.categories.ChatCategoryFormat

class ChatCategoryAdapter(private val chatCategoryList: ArrayList<ChatCategoryFormat>) : RecyclerView.Adapter<ChatCategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_chat_search, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return chatCategoryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
        if (chatCategoryList[p1].isMine == true) {
            holder.user_message_date.setBackgroundColor(Color.LTGRAY)
            holder.user_message.setBackgroundColor(Color.LTGRAY)
        } else {
            holder.user_message_date.setBackgroundColor(Color.WHITE)
            holder.user_message.setBackgroundColor(Color.WHITE)
        }
        holder.user_message.text = chatCategoryList[p1].content
        holder.user_message_date.text = chatCategoryList[p1].date
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var user_message: TextView
        internal var user_message_date: TextView

        init {
            user_message = itemView.findViewById(R.id.search_message)
            user_message_date = itemView.findViewById(R.id.search_message_date)
        }
    }
}
