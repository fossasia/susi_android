package org.fossasia.susi.ai.skills.skillSearch

import android.content.Context
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SnapHelper
import android.view.View
import kotlinx.android.synthetic.main.fragment_group_wise_skill_listing.*
import kotlinx.android.synthetic.main.fragment_group_wise_skill_listing.errorSkillFetch
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.dataclasses.GroupWiseSkills
import org.fossasia.susi.ai.helper.BaseFragment
import org.fossasia.susi.ai.helper.SimpleDividerItemDecoration
import org.fossasia.susi.ai.helper.StartSnapHelper
import org.fossasia.susi.ai.rest.responses.susi.SkillData
import org.fossasia.susi.ai.skills.SkillFragmentCallback
import org.fossasia.susi.ai.skills.groupwiseskills.adapters.recycleradapters.SkillsListAdapter
import org.fossasia.susi.ai.skills.groupwiseskills.contract.IGroupWiseSkillsPresenter
import org.fossasia.susi.ai.skills.groupwiseskills.contract.IGroupWiseSkillsView
import timber.log.Timber

/**
 *
 * Created by naman653 on 28/03/2019.
 */
class SearchSkillFragment : BaseFragment(), IGroupWiseSkillsView, SwipeRefreshLayout.OnRefreshListener {
    override fun getTitle(): String {
        return this.skills.group // To change body of created functions use File | Settings | File Templates.
    }

    override val rootLayout: Int
        get() = R.layout.fragment_group_wise_skill_listing // To change initializer of created properties use File | Settings | File Templates.

    private lateinit var query: String
    private lateinit var skillSet: List<SkillData>
    private lateinit var skillAdapterSnapHelper: SnapHelper
    private lateinit var searchSkillPresenter: IGroupWiseSkillsPresenter
    private lateinit var skills: GroupWiseSkills
    private lateinit var skillsAdapter: SkillsListAdapter
    private lateinit var skillCallback: SkillFragmentCallback

    companion object {
        const val SEARCH_KEY = "search_key"
        const val SKILL_DATA = "skill_data"
        fun newInstance(skillData: ArrayList<SkillData>, searchQuery: String): SearchSkillFragment {
            val fragment = SearchSkillFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList(SKILL_DATA, skillData)
            bundle.putString(SEARCH_KEY, searchQuery)
            fragment.arguments = bundle

            return fragment
        }
    }

    @NonNull
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        query = arguments?.getString(SEARCH_KEY).toString()
        skillSet = arguments?.getParcelableArrayList<SkillData>(SKILL_DATA) as List<SkillData>
        skills = GroupWiseSkills(query, skillSet.toMutableList())

        searchSkillPresenter = SearchSkillPresenter(this, skills)
        searchSkillPresenter.onAttach(this)
        initRefreshScreen(swipeRefreshLayout.id, this)
        setUPAdapter()
        searchSkillPresenter.getSkills(isRefreshing(), skills.group)
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setUPAdapter() {
        initShimmerLayout(groupWiseSkillsShimmerContainer.id)
        skillAdapterSnapHelper = StartSnapHelper()
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        groupWiseSkills.layoutManager = layoutManager
        skillsAdapter = SkillsListAdapter(requireContext(), skills, skillCallback)
        Timber.d(skills.toString())
        groupWiseSkills.adapter = skillsAdapter
        groupWiseSkills.onFlingListener = null
        skillAdapterSnapHelper.attachToRecyclerView(groupWiseSkills)
        if (isShimmerStarted()) {
            stopShimmer()
        }
    }

    override fun visibilityProgressBar(boolean: Boolean) {
        setVisibilityProgressBar(boolean)
    }

    override fun showEmptySkillsListMessage() {
        if (activity != null) {
            stopRefreshing()
            groupWiseSkills.visibility = View.GONE
            messageNoSkillsFound.visibility = View.VISIBLE
        }
    }

    override fun displayError() {
        if (activity != null) {
            stopRefreshing()
            groupWiseSkills.visibility = View.GONE
            errorSkillFetch.visibility = View.VISIBLE
        }
    }

    override fun updateAdapter(skills: GroupWiseSkills) {
        stopRefreshing()
        if (errorSkillFetch.visibility == View.VISIBLE) {
            errorSkillFetch.visibility = View.GONE
        }
        groupWiseSkills.visibility = View.VISIBLE
        if (isShimmerStarted()) {
            stopShimmer()
        }

        groupWiseSkills.addItemDecoration(SimpleDividerItemDecoration(requireContext(), this.skills.skillsList.size))
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
        searchSkillPresenter.getSkills(isRefreshing(), skills.group)
    }

    override fun onDestroyView() {
        searchSkillPresenter.onDetach()
        super.onDestroyView()
    }
}
