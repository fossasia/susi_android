package org.fossasia.susi.ai.chat.adapters.recycleradapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_voice_commands.view.*
import org.fossasia.susi.ai.R

class VoiceCommandsAdapter(val items: ArrayList<String>, val context: Context?) : RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_voice_commands, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.voiceCommand?.text = items.get(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val voiceCommand = view.voiceCommand
}
