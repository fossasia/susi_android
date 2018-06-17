package org.fossasia.susi.ai.skills.skilllisting.adapters.recycleradapters

import android.content.Context
import android.support.annotation.NonNull
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SnapHelper
import android.view.LayoutInflater
import android.view.ViewGroup
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.helper.StartSnapHelper
import org.fossasia.susi.ai.rest.responses.susi.SkillData
import org.fossasia.susi.ai.skills.SkillFragmentCallback
import org.fossasia.susi.ai.skills.skilllisting.adapters.viewholders.GroupViewHolder

/**
 *
 * Created by chiragw15 on 15/8/17.
 */
class SkillGroupAdapter(val context: Context, val skills: ArrayList<Pair<String, List<SkillData>>>, val skillCallback: SkillFragmentCallback)
    : RecyclerView.Adapter<GroupViewHolder>() {

    private lateinit var skillAdapterSnapHelper: SnapHelper

    @NonNull
    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        if (skills[position].first != null)
            holder.groupName?.text = skills[position].first

        skillAdapterSnapHelper = StartSnapHelper()
        holder.skillList?.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        holder.skillList?.layoutManager = mLayoutManager
        holder.skillList?.adapter = SkillListAdapter(context, skills[position], skillCallback)
        holder.skillList?.onFlingListener = null
        skillAdapterSnapHelper.attachToRecyclerView(holder.skillList)
    }

    override fun getItemCount(): Int {
        return skills.size
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_skill_group, parent, false)
        return GroupViewHolder(itemView)
    }
}