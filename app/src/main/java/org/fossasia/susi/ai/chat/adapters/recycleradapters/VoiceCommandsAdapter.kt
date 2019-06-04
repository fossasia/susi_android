package org.fossasia.susi.ai.chat.adapters.recycleradapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.item_voice_commands.view.voiceCommand
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.ChatActivity
import org.fossasia.susi.ai.chat.ChatPresenter
import org.fossasia.susi.ai.chat.contract.IChatPresenter

class VoiceCommandsAdapter(val items: ArrayList<String>, val context: Context?) : RecyclerView.Adapter<VoiceCommandsAdapter.ViewHolder>() {
    lateinit var chatPresenter: IChatPresenter

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        chatPresenter = ChatPresenter(context as Context)
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_voice_commands, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder?.voiceCommand?.text = items.get(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val voiceCommand: TextView = view.voiceCommand

        init {
            voiceCommand.setOnClickListener {
                val message = items[adapterPosition]
                val intent = Intent(context, ChatActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra("example", message)
                context?.startActivity(intent)
            }
        }
    }
}