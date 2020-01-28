package org.fossasia.susi.ai.skills.groupwiseskills

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.content.Context
import android.databinding.DataBindingUtil
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
import kotlin.collections.ArrayList
import kotlinx.android.synthetic.main.fragment_group_wise_skill_listing.*
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.databinding.FragmentGroupWiseSkillListingBinding
import org.fossasia.susi.ai.dataclasses.GroupWiseSkills
import org.fossasia.susi.ai.helper.ListHelper
import org.fossasia.susi.ai.helper.SimpleDividerItemDecoration
import org.fossasia.susi.ai.helper.StartSnapHelper
import org.fossasia.susi.ai.rest.responses.susi.SkillData
import org.fossasia.susi.ai.skills.SkillFragmentCallback
import org.fossasia.susi.ai.skills.groupwiseskills.adapters.recycleradapters.SkillsListAdapter
import org.fossasia.susi.ai.skills.groupwiseskills.contract.IGroupWiseSkillsPresenter
import org.fossasia.susi.ai.skills.groupwiseskills.contract.IGroupWiseSkillsView
import org.fossasia.susi.ai.skills.skillSearch.SearchSkillFragment
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
    private lateinit var shimmerContainer: ShimmerFrameLayout
    private var _isSearching = MutableLiveData<Boolean>()
    val isSearching: LiveData<Boolean>
        get() = _isSearching

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
        groupWiseSkillsPresenter = GroupWiseSkillsPresenter(this)
        _isSearching = MutableLiveData()
        if (argument != null) {
            this.skills.group = argument.getString("group")
        }
        val binding: FragmentGroupWiseSkillListingBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_group_wise_skill_listing, container, false)
        binding.lifecycleOwner = this // use Fragment.viewLifecycleOwner for fragments
        binding.presenter = groupWiseSkillsPresenter as GroupWiseSkillsPresenter
        binding.fragment = this
        return binding.root
    }

    @NonNull
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.title = this.skills.group
        (groupWiseSkillsPresenter as GroupWiseSkillsPresenter).shimmerController.value = true
        groupWiseSkillsPresenter.onAttach(this)
        (groupWiseSkillsPresenter as GroupWiseSkillsPresenter).shimmerController.postValue(true)
        swipeRefreshLayout.setOnRefreshListener(this)
        setUPAdapter()
        groupWiseSkillsPresenter.getSkills(swipeRefreshLayout.isRefreshing, skills.group)
        _isSearching.value = false
        searchSkillGroupWise.setOnClickListener {
            handleSearchAction()
        }

        (groupWiseSkillsPresenter as GroupWiseSkillsPresenter).swipeRefreshController.observe(this, Observer { })
        (groupWiseSkillsPresenter as GroupWiseSkillsPresenter).shimmerController.observe(this, Observer<Boolean> { t ->
        }
        )
        _isSearching.observe(this, Observer { })
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
    }

    fun handleSearchAction() {
        if (_isSearching.value!!) {
            _isSearching.value = false
        } else {
            _isSearching.value = true
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
        _isSearching.value = true
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
        _isSearching.value = false

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
            groupWiseSkills.visibility = View.GONE
            messageNoSkillsFound.visibility = View.VISIBLE
        }
    }

    override fun displayError() {
        if (activity != null) {
            groupWiseSkills.visibility = View.GONE
            errorSkillFetch.visibility = View.VISIBLE
        }
    }

    override fun updateAdapter(skills: GroupWiseSkills) {
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

    fun revertSkillList() {
        val revertedList = ListHelper.revertList(skills.skillsList)
        skills.skillsList.clear()
        skills.skillsList.addAll(revertedList)
        skillsAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        activity?.title = this.skills.group
    }
}
