package org.fossasia.susi.ai.skills.skilldetails.adapters.recycleradapters

import android.content.Context
import android.support.annotation.NonNull
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.skills.skillactivity.SkillFragmentCallback
import org.fossasia.susi.ai.skills.skilldetails.adapters.viewholders.SkillExampleViewHolder

/**
 *
 * Created by chiragw15 on 27/8/17.
 */
class SkillExamplesAdapter(val context: Context, val examples: List<String>,val skillCallback: SkillFragmentCallback) : RecyclerView.Adapter<SkillExampleViewHolder>(),
        SkillExampleViewHolder.ClickListener {

    private val clickListener: SkillExampleViewHolder.ClickListener = this

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillExampleViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_example_skill, parent, false)
        return SkillExampleViewHolder(itemView, clickListener)
    }

    override fun getItemCount(): Int {
        return examples.size
    }

    @NonNull
    override fun onBindViewHolder(holder: SkillExampleViewHolder, position: Int) {
        if (examples[position] != null && !examples[position].isEmpty()) {
            holder.example?.text = examples[position]
        }
    }

    override fun onItemClicked(position: Int) {
            skillCallback.startChatActivity(position, examples)
    }
}
