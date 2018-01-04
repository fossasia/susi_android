package org.fossasia.susi.ai.skills.skilllisting.adapters.recycleradapters

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SnapHelper
import android.view.ViewGroup
import org.fossasia.susi.ai.rest.responses.susi.SkillData
import org.fossasia.susi.ai.skills.skilllisting.adapters.viewholders.GroupViewHolder
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.fragment_skill_listing.*
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.helper.StartSnapHelper

/**
 *
 * Created by chiragw15 on 15/8/17.
 */
class SkillGroupAdapter(val context: Context, val skills: ArrayList<Pair<String, Map<String, SkillData>>>)
        :RecyclerView.Adapter<GroupViewHolder>() {

    lateinit var skillAdapterSnapHelper : SnapHelper

    override fun onBindViewHolder(holder: GroupViewHolder?, position: Int) {
        if(skills[position].first != null)
            holder?.groupName?.text = skills[position].first

        skillAdapterSnapHelper = StartSnapHelper()
        holder?.skillList?.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        holder?.skillList?.layoutManager = mLayoutManager
        holder?.skillList?.adapter = SkillListAdapter(context, skills[position])
        holder?.skillList?.setOnFlingListener(null)
        skillAdapterSnapHelper.attachToRecyclerView(holder?.skillList)
    }

    override fun getItemCount(): Int {
        return skills.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): GroupViewHolder {
        val itemView = LayoutInflater.from(parent?.context)
                .inflate(R.layout.item_skill_group, parent, false)
        return GroupViewHolder(itemView)
    }
}