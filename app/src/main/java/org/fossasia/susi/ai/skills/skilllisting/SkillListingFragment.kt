package org.fossasia.susi.ai.skills.skilllisting

import android.content.Context
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SnapHelper
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_skill_listing.*
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.helper.StartSnapHelper
import org.fossasia.susi.ai.rest.responses.susi.SkillData
import org.fossasia.susi.ai.skills.SkillFragmentCallback
import org.fossasia.susi.ai.skills.SkillsActivity
import org.fossasia.susi.ai.skills.skilllisting.adapters.recycleradapters.SkillGroupAdapter
import org.fossasia.susi.ai.skills.skilllisting.contract.ISkillListingPresenter
import org.fossasia.susi.ai.skills.skilllisting.contract.ISkillListingView

/**
 *
 * Created by chiragw15 on 15/8/17.
 */
class SkillListingFragment : Fragment(), ISkillListingView, SwipeRefreshLayout.OnRefreshListener {

    private lateinit var skillAdapterSnapHelper: SnapHelper
    private lateinit var skillListingPresenter: ISkillListingPresenter
    var skills: ArrayList<Pair<String, List<SkillData>>> = ArrayList()
    private lateinit var skillGroupAdapter: SkillGroupAdapter
    private lateinit var skillCallback: SkillFragmentCallback

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_skill_listing, container, false)
    }

    @NonNull
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as SkillsActivity).title = (activity as SkillsActivity).getString(R.string.skills_activity)
        skillListingPresenter = SkillListingPresenter()
        skillListingPresenter.onAttach(this)
        swipe_refresh_layout.setOnRefreshListener(this)
        setUPAdapter()
        skillListingPresenter.getGroups(swipe_refresh_layout.isRefreshing)
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setUPAdapter() {
        skillAdapterSnapHelper = StartSnapHelper()
        val mLayoutManager = LinearLayoutManager(activity)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        skillGroups.layoutManager = mLayoutManager
        skillGroupAdapter = SkillGroupAdapter(requireContext(), skills, skillCallback)
        skillGroups.adapter = skillGroupAdapter
        skillGroups.onFlingListener = null
        skillAdapterSnapHelper.attachToRecyclerView(skillGroups)
    }

    override fun visibilityProgressBar(boolean: Boolean) {
        if (boolean) progressSkillWait.visibility = View.VISIBLE else progressSkillWait.visibility = View.GONE
    }

    override fun displayError() {
        if (activity != null) {
            swipe_refresh_layout.isRefreshing = false
            skillGroups.visibility = GONE
            errorSkillFetch.visibility = View.VISIBLE
        }
    }

    override fun updateAdapter(skills: ArrayList<Pair<String, List<SkillData>>>) {
        swipe_refresh_layout.isRefreshing = false
        if (errorSkillFetch.visibility == View.VISIBLE) {
            errorSkillFetch.visibility = View.GONE
        }
        this.skills.clear()
        this.skills.addAll(skills)
        skillGroupAdapter.notifyDataSetChanged()
    }

    override fun onRefresh() {
        setUPAdapter()
        skillListingPresenter.getGroups(swipe_refresh_layout.isRefreshing)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context);
        skillCallback = activity as SkillFragmentCallback
    }

    override fun onDestroyView() {
        skillListingPresenter.onDetach()
        super.onDestroyView()
    }
}