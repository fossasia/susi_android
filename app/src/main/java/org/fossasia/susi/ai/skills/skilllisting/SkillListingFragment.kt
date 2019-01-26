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
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_skill_listing.swipe_refresh_layout
import kotlinx.android.synthetic.main.fragment_skill_listing.skillMetrics
import kotlinx.android.synthetic.main.fragment_skill_listing.progressSkillWait
import kotlinx.android.synthetic.main.fragment_skill_listing.errorSkillFetch
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.dataclasses.SkillsBasedOnMetrics
import org.fossasia.susi.ai.helper.StartSnapHelper
import org.fossasia.susi.ai.rest.responses.susi.SkillData
import org.fossasia.susi.ai.skills.SkillFragmentCallback
import org.fossasia.susi.ai.skills.skilllisting.adapters.recycleradapters.SkillMetricsAdapter
import org.fossasia.susi.ai.skills.skilllisting.contract.ISkillListingPresenter
import org.fossasia.susi.ai.skills.skilllisting.contract.ISkillListingView
import org.fossasia.susi.ai.helper.SimpleDividerItemDecoration
import timber.log.Timber

/**
 *
 * Created by chiragw15 on 15/8/17.
 */
class SkillListingFragment : Fragment(), ISkillListingView, SwipeRefreshLayout.OnRefreshListener {

    private lateinit var skillAdapterSnapHelper: SnapHelper
    private lateinit var skillListingPresenter: ISkillListingPresenter
    var skills: ArrayList<Pair<String, List<SkillData>>> = ArrayList()
    private var metrics = SkillsBasedOnMetrics(ArrayList(), ArrayList(), ArrayList())
    private lateinit var skillMetricsAdapter: SkillMetricsAdapter
    private lateinit var skillCallback: SkillFragmentCallback

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_skill_listing, container, false)
    }

    @NonNull
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.title = getString(R.string.skills_activity)
        skillListingPresenter = SkillListingPresenter(this)
        skillListingPresenter.onAttach(this)
        swipe_refresh_layout.setOnRefreshListener(this)
        setUPAdapter()
        skillListingPresenter.getMetrics(swipe_refresh_layout.isRefreshing)
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setUPAdapter() {
        skillAdapterSnapHelper = StartSnapHelper()
        val mLayoutManager = LinearLayoutManager(activity)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        skillMetrics.layoutManager = mLayoutManager
        skillMetricsAdapter = SkillMetricsAdapter(requireContext(), metrics, skillCallback)
        skillMetrics.adapter = skillMetricsAdapter
        skillMetrics.onFlingListener = null
        skillAdapterSnapHelper.attachToRecyclerView(skillMetrics)
    }

    override fun visibilityProgressBar(boolean: Boolean) {
        if (boolean) progressSkillWait.visibility = View.VISIBLE else progressSkillWait.visibility = View.GONE
    }

    override fun displayError() {
        if (activity != null) {
            swipe_refresh_layout.isRefreshing = false
            skillMetrics.visibility = View.GONE
            errorSkillFetch.visibility = View.VISIBLE
        }
    }

    override fun updateAdapter(metrics: SkillsBasedOnMetrics) {
        swipe_refresh_layout.isRefreshing = false
        if (errorSkillFetch.visibility == View.VISIBLE) {
            errorSkillFetch.visibility = View.GONE
        }
        skillMetrics.visibility = View.VISIBLE
        this.metrics.metricsList.clear()
        this.metrics.metricsGroupTitles.clear()
        this.metrics.groups.clear()
        this.metrics.metricsList.addAll(metrics.metricsList)
        this.metrics.metricsGroupTitles.addAll(metrics.metricsGroupTitles)
        this.metrics.groups.add(0, "CATEGORIES")
        this.metrics.groups.add(1, "All")
        this.metrics.groups.addAll(metrics.groups)
        skillMetrics.addItemDecoration(SimpleDividerItemDecoration(requireContext(), this.metrics.metricsList.size))
        skillMetricsAdapter.notifyDataSetChanged()
    }

    override fun onRefresh() {
        setUPAdapter()
        skillListingPresenter.getMetrics(swipe_refresh_layout.isRefreshing)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SkillFragmentCallback) {
            skillCallback = context
        } else {
            Timber.e("context is not SkillFragmentCallback")
        }
    }

    override fun onDestroyView() {
        skillListingPresenter.onDetach()
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        activity?.title = getString(R.string.skills_activity)
    }
}