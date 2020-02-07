package org.fossasia.susi.ai.skills.groupwiseskills

import android.content.Context
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SnapHelper
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.facebook.shimmer.ShimmerFrameLayout
import kotlinx.android.synthetic.main.fragment_group_wise_skill_listing.*
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.dataclasses.GroupWiseSkills
import org.fossasia.susi.ai.helper.SimpleDividerItemDecoration
import org.fossasia.susi.ai.helper.StartSnapHelper
import org.fossasia.susi.ai.rest.responses.susi.SkillData
import org.fossasia.susi.ai.skills.SkillFragmentCallback
import org.fossasia.susi.ai.skills.groupwiseskills.adapters.recycleradapters.SkillsListAdapter
import org.fossasia.susi.ai.skills.groupwiseskills.contract.IGroupWiseSkillsPresenter
import org.fossasia.susi.ai.skills.groupwiseskills.contract.IGroupWiseSkillsView
import org.fossasia.susi.ai.skills.skillSearch.SearchSkillFragment
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import timber.log.Timber

/**
 *
 * Created by arundhati24 on 16/07/2018.
 */
class GroupWiseSkillsFragment : Fragment(), IGroupWiseSkillsView, SwipeRefreshLayout.OnRefreshListener {
    private lateinit var skillAdapterSnapHelper: SnapHelper
    private val groupWiseSkillsPresenter: IGroupWiseSkillsPresenter by inject { parametersOf(this) }
    private var skills = GroupWiseSkills("", ArrayList())
    private lateinit var skillsAdapter: SkillsListAdapter
    private lateinit var skillCallback: SkillFragmentCallback
    private lateinit var shimmerContainer: ShimmerFrameLayout
    private var isSearching: Boolean = false

    companion object {
        private const val SKILL_GROUP = "group"
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
        val rootView: View = inflater.inflate(R.layout.fragment_group_wise_skill_listing, container, false)
        shimmerContainer = rootView.findViewById(R.id.groupWiseSkillsShimmerContainer)
        shimmerContainer.visibility = View.VISIBLE
        shimmerContainer.startShimmer()
        return rootView
    }

    @NonNull
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.title = this.skills.group
        swipeRefreshLayout.setOnRefreshListener(this)
        setUPAdapter()
        groupWiseSkillsPresenter.getSkills(swipeRefreshLayout.isRefreshing, skills.group)
        isSearching = true
        skillWiseSearchEdit.visibility = View.GONE
        searchSkillGroupWise.visibility = View.VISIBLE
        searchSkillGroupWise.setOnClickListener {
            handleSearchAction()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setUPAdapter() {
        skillAdapterSnapHelper = StartSnapHelper()
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        groupWiseSkills.layoutManager = layoutManager
        skillsAdapter = SkillsListAdapter(requireContext(), skills, skillCallback)
        groupWiseSkills.adapter = skillsAdapter
        groupWiseSkills.onFlingListener = null
        skillAdapterSnapHelper.attachToRecyclerView(groupWiseSkills)
    }

    override fun visibilityProgressBar(boolean: Boolean) {
        if (boolean) {
            shimmerContainer.visibility = View.VISIBLE
            shimmerContainer.startShimmer()
        } else {
            shimmerContainer.stopShimmer()
            shimmerContainer.visibility = View.GONE
        }
    }

    fun handleSearchAction() {
        if (isSearching == false || skillWiseSearchEdit.getVisibility() == View.VISIBLE) {
            skillWiseSearchEdit.setVisibility(View.GONE)
        } else {
            skillWiseSearchEdit.setVisibility(View.VISIBLE)
            handleSearch()
        }
    }

    fun handleSearch() {
        skillWiseSearchEdit?.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP && skillWiseSearchEdit?.text.toString().isNotEmpty()) {
                performSearch(skillWiseSearchEdit?.text.toString())
            }
            true
        }
        skillWiseSearchEdit?.requestFocus()
        isSearching = true
    }

    fun performSearch(query: String): Boolean {

        var searchedSkillsList: ArrayList<SkillData> = arrayListOf()

        if (skills.skillsList.isEmpty()) {
            Toast.makeText(context, R.string.skill_empty, Toast.LENGTH_SHORT).show()
            return true
        }

        for (skill in skills.skillsList) {
            if (skill.skillName != "" && skill.skillName != null) {
                if (skill.skillName.toLowerCase().contains(query.toLowerCase())) {
                    searchedSkillsList.add(skill)
                }
            }
        }
        Timber.d(searchedSkillsList.toString())
        if (searchedSkillsList.isEmpty()) {
            Toast.makeText(context, R.string.skill_not_found, Toast.LENGTH_SHORT).show()
            return false
        }

        loadSearchSkillsFragment(searchedSkillsList, query)
        return true
    }

    private fun loadSearchSkillsFragment(searchedSkills: ArrayList<SkillData>, searchQuery: String) {
        val skillSearchFragment = SearchSkillFragment.newInstance(searchedSkills, searchQuery)
        fragmentManager?.beginTransaction()
                ?.add(R.id.fragment_container, skillSearchFragment)
                ?.addToBackStack(SearchSkillFragment().toString())
                ?.commit()
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
        skills.skillsList.clear()
        skillsAdapter.notifyDataSetChanged()
        messageNoSkillsFound.visibility = View.GONE
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
