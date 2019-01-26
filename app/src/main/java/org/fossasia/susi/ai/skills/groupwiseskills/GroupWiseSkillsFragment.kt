package org.fossasia.susi.ai.skills.groupwiseskills

import android.content.Context
import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SnapHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_group_wise_skill_listing.groupWiseSkills
import kotlinx.android.synthetic.main.fragment_group_wise_skill_listing.swipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_group_wise_skill_listing.progressSkillWait
import kotlinx.android.synthetic.main.fragment_group_wise_skill_listing.messageNoSkillsFound
import kotlinx.android.synthetic.main.fragment_group_wise_skill_listing.errorSkillFetch
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.dataclasses.GroupWiseSkills
import org.fossasia.susi.ai.helper.SimpleDividerItemDecoration
import org.fossasia.susi.ai.helper.StartSnapHelper
import org.fossasia.susi.ai.skills.SkillFragmentCallback
import org.fossasia.susi.ai.skills.groupwiseskills.adapters.recycleradapters.SkillsListAdapter
import org.fossasia.susi.ai.skills.groupwiseskills.contract.IGroupWiseSkillsPresenter
import org.fossasia.susi.ai.skills.groupwiseskills.contract.IGroupWiseSkillsView
import timber.log.Timber

/**
 *
 * Created by arundhati24 on 16/07/2018.
 */
class GroupWiseSkillsFragment : Fragment(), IGroupWiseSkillsView, SwipeRefreshLayout.OnRefreshListener {
    private lateinit var skillAdapterSnapHelper: SnapHelper
    private lateinit var groupWiseSkillsPresenter: IGroupWiseSkillsPresenter
    private var skills = GroupWiseSkills("", ArrayList())
    private lateinit var skillsAdapter: SkillsListAdapter
    private lateinit var skillCallback: SkillFragmentCallback

    companion object {
        const val SKILL_GROUP = "group"
        fun newInstance(group: String): GroupWiseSkillsFragment {
            val fragment = GroupWiseSkillsFragment()
            val bundle = Bundle()
            bundle.putString(SKILL_GROUP, group)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val argument = arguments
        if (argument != null) {
            this.skills.group = argument.getString("group")
        }
        return inflater.inflate(R.layout.fragment_group_wise_skill_listing, container, false)
    }

    @NonNull
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.title = this.skills.group
        groupWiseSkillsPresenter = GroupWiseSkillsPresenter(this)
        groupWiseSkillsPresenter.onAttach(this)
        swipeRefreshLayout.setOnRefreshListener(this)
        setUPAdapter()
        groupWiseSkillsPresenter.getSkills(swipeRefreshLayout.isRefreshing, skills.group)
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setUPAdapter() {
        skillAdapterSnapHelper = StartSnapHelper()
        val mLayoutManager = LinearLayoutManager(activity)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        groupWiseSkills.layoutManager = mLayoutManager
        skillsAdapter = SkillsListAdapter(requireContext(), skills, skillCallback)
        groupWiseSkills.adapter = skillsAdapter
        groupWiseSkills.onFlingListener = null
        skillAdapterSnapHelper.attachToRecyclerView(groupWiseSkills)
    }

    override fun visibilityProgressBar(boolean: Boolean) {
        progressSkillWait.visibility = if (boolean) View.VISIBLE else View.GONE
    }

    override fun showEmptySkillsListMessage() {
        if (activity != null) {
            swipeRefreshLayout.isRefreshing = false
            groupWiseSkills.visibility = View.GONE
            messageNoSkillsFound.visibility = View.VISIBLE
        }
    }

    override fun displayError() {
        if (activity != null) {
            swipeRefreshLayout.isRefreshing = false
            groupWiseSkills.visibility = View.GONE
            errorSkillFetch.visibility = View.VISIBLE
        }
    }

    override fun updateAdapter(skills: GroupWiseSkills) {
        swipeRefreshLayout.isRefreshing = false
        if (errorSkillFetch.visibility == View.VISIBLE) {
            errorSkillFetch.visibility = View.GONE
        }
        groupWiseSkills.visibility = View.VISIBLE
        this.skills.skillsList.clear()
        this.skills.skillsList.addAll(skills.skillsList)
        groupWiseSkills.addItemDecoration(SimpleDividerItemDecoration(requireContext(), this.skills.skillsList.size))
        skillsAdapter.notifyDataSetChanged()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SkillFragmentCallback) {
            skillCallback = context
        } else {
            Timber.e("context is not SkillFragmentCallback")
        }
    }

    override fun onRefresh() {
        setUPAdapter()
        groupWiseSkillsPresenter.getSkills(swipeRefreshLayout.isRefreshing, skills.group)
    }

    override fun onDestroyView() {
        groupWiseSkillsPresenter.onDetach()
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        activity?.title = this.skills.group
    }
}