package org.fossasia.susi.ai.skills.skilllisting.adapters.recycleradapters

import android.content.Context
import android.support.annotation.NonNull
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SnapHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.dataclasses.SkillsBasedOnMetrics
import org.fossasia.susi.ai.helper.StartSnapHelper
import org.fossasia.susi.ai.skills.SkillFragmentCallback
import org.fossasia.susi.ai.skills.skilllisting.adapters.viewholders.GroupViewHolder
import org.fossasia.susi.ai.skills.skilllisting.adapters.viewholders.SkillGroupViewHolder

class SkillMetricsAdapter(val context: Context, val metrics: SkillsBasedOnMetrics, val skillCallback: SkillFragmentCallback) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(), SkillGroupViewHolder.ClickListener {

    private val VIEW_TYPE_METRIC = 0
    private val VIEW_TYPE_GROUP = 1
    private lateinit var skillAdapterSnapHelper: SnapHelper

    private val clickListener: SkillGroupViewHolder.ClickListener = this

    @NonNull
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is GroupViewHolder) {
            if (metrics.metricsList[position] != null) {
                holder.groupName.text = metrics.metricsGroupTitles[position]
            }

            skillAdapterSnapHelper = StartSnapHelper()
            holder.skillList.setHasFixedSize(true)
            val mLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            holder.skillList.layoutManager = mLayoutManager
            holder.skillList.adapter = SkillListAdapter(context, metrics.metricsList[position], skillCallback)
            holder.skillList.onFlingListener = null
            skillAdapterSnapHelper.attachToRecyclerView(holder.skillList)
        }

        if (holder is SkillGroupViewHolder) {
            if (position - metrics.metricsList.size == 0) {
                holder.groupParent.setBackgroundColor(ContextCompat.getColor(context, R.color.default_bg))
                holder.groupName.setBackgroundColor(ContextCompat.getColor(context, R.color.default_bg))
                holder.arrowIcon.visibility = View.GONE
            } else {
                holder.groupParent.setBackgroundColor(ContextCompat.getColor(context, R.color.md_white_1000))
                holder.groupName.setBackgroundColor(ContextCompat.getColor(context, R.color.md_white_1000))
                holder.arrowIcon.visibility = View.VISIBLE
            }
            holder.groupName.text = metrics.groups[position - metrics.metricsList.size]
        }
    }

    override fun getItemCount(): Int {
        return metrics.metricsList.size + metrics.groups.size
    }

    override fun getItemViewType(position: Int): Int {
        if (position < metrics.metricsList.size) {
            return VIEW_TYPE_METRIC
        }

        if (position - metrics.metricsList.size < metrics.groups.size) {
            return VIEW_TYPE_GROUP
        }

        return -1
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_METRIC) {
            val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_skill_group, parent, false)
            GroupViewHolder(itemView)
        } else {
            val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_skill_group_list, parent, false)
            SkillGroupViewHolder(itemView, metrics.metricsList.size, clickListener)
        }
    }

    override fun onItemClicked(position: Int) {
        if (position == 0) {
            return
        }
        showGroupWiseSkillsFragment(metrics.groups[position])
    }

    private fun showGroupWiseSkillsFragment(group: String) {
        skillCallback.loadGroupWiseSkillsFragment(group)
    }
}