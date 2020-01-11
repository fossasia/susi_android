package org.fossasia.susi.ai.helper

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import butterknife.Unbinder
import com.facebook.shimmer.ShimmerFrameLayout

abstract class BaseFragment : Fragment(), IBaseListingView {
    private var unbinder: Unbinder? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null

    internal abstract fun getTitle(): String
    private var shimmerContainer: ShimmerFrameLayout? = null

    @get:LayoutRes
    protected abstract val rootLayout: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(rootLayout, container, false)
        unbinder = ButterKnife.bind(this, view)

        return view
    }

    protected fun initRefreshScreen(@IdRes viewIdSwipe: Int, swipeRefreshLayoutListener: SwipeRefreshLayout.OnRefreshListener?) {
        if (getView() == null)
            return
        swipeRefreshLayout = getView()?.findViewById(viewIdSwipe)
        swipeRefreshLayout!!.setOnRefreshListener(swipeRefreshLayoutListener)
    }

    protected fun initShimmerLayout(@IdRes viewIdSwipe: Int) {

        shimmerContainer = getView()?.findViewById(viewIdSwipe)
        shimmerContainer?.visibility = View.VISIBLE
    }

    protected fun setTitle(title: String) {
        activity?.title = title
    }

    override fun onResume() {
        super.onResume()
        setTitle(getTitle())
    }

    protected fun isRefreshing(): Boolean {
        return swipeRefreshLayout!!.isRefreshing
    }

    protected fun startRefreshing() {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout!!.setRefreshing(true)
    }

    protected fun stopRefreshing() {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout!!.setRefreshing(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder!!.unbind()
    }

    override fun displayError() {
    }

    protected fun startShimmer() {
        if (shimmerContainer != null)
            shimmerContainer!!.startShimmer()
        shimmerContainer!!.visibility = View.VISIBLE
    }

    protected fun stopShimmer() {
        shimmerContainer!!.stopShimmer()
        shimmerContainer!!.visibility = View.GONE
    }

    override fun setVisibilityProgressBar(boolean: Boolean) {
        if (boolean) {
            shimmerContainer?.visibility = View.VISIBLE
            shimmerContainer?.startShimmer()
        } else {
            shimmerContainer?.stopShimmer()
            shimmerContainer?.visibility = View.GONE
        }
    }

    fun isShimmerStarted(): Boolean {
        return shimmerContainer!!.isShimmerStarted
    }
}
