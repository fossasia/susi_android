package org.fossasia.susi.ai.helper

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import android.view.View

/**
 * Created by robinkamboj on 04/01/18.
 */

class StartSnapHelper : androidx.recyclerview.widget.LinearSnapHelper() {

    private var verticalHelper: androidx.recyclerview.widget.OrientationHelper? = null
    private var horizontalHelper: androidx.recyclerview.widget.OrientationHelper? = null

    @Throws(IllegalStateException::class)
    override fun attachToRecyclerView(recyclerView: androidx.recyclerview.widget.RecyclerView?) {
        super.attachToRecyclerView(recyclerView)
    }

    override fun calculateDistanceToFinalSnap(
            layoutManager: androidx.recyclerview.widget.RecyclerView.LayoutManager,
            targetView: View
    ): IntArray? {
        val out = IntArray(2)

        if (layoutManager.canScrollHorizontally()) {
            out[0] = distanceToStart(targetView, getHorizontalHelper(layoutManager))
        } else {
            out[0] = 0
        }

        if (layoutManager.canScrollVertically()) {
            out[1] = distanceToStart(targetView, getVerticalHelper(layoutManager))
        } else {
            out[1] = 0
        }
        return out
    }

    override fun findSnapView(layoutManager: androidx.recyclerview.widget.RecyclerView.LayoutManager): View? {

        return if (layoutManager is androidx.recyclerview.widget.LinearLayoutManager) {

            if (layoutManager.canScrollHorizontally()) {
                getStartView(layoutManager, getHorizontalHelper(layoutManager))
            } else {
                getStartView(layoutManager, getVerticalHelper(layoutManager))
            }
        } else super.findSnapView(layoutManager)
    }

    private fun distanceToStart(targetView: View, helper: androidx.recyclerview.widget.OrientationHelper): Int {
        return helper.getDecoratedStart(targetView) - helper.startAfterPadding
    }

    private fun getStartView(
            layoutManager: androidx.recyclerview.widget.RecyclerView.LayoutManager,
            helper: androidx.recyclerview.widget.OrientationHelper
    ): View? {

        if (layoutManager is androidx.recyclerview.widget.LinearLayoutManager) {
            val firstChild = layoutManager.findFirstVisibleItemPosition()

            val isLastItem = layoutManager
                    .findLastCompletelyVisibleItemPosition() == layoutManager.getItemCount() - 1

            if (firstChild == androidx.recyclerview.widget.RecyclerView.NO_POSITION || isLastItem) {
                return null
            }

            val child = layoutManager.findViewByPosition(firstChild)

            return if (helper.getDecoratedEnd(child) >= helper.getDecoratedMeasurement(child) / 2 && helper.getDecoratedEnd(child) > 0) {
                child
            } else {
                if (layoutManager.findLastCompletelyVisibleItemPosition() == layoutManager.getItemCount() - 1) {
                    null
                } else {
                    layoutManager.findViewByPosition(firstChild + 1)
                }
            }
        }

        return super.findSnapView(layoutManager)
    }

    private fun getVerticalHelper(layoutManager: androidx.recyclerview.widget.RecyclerView.LayoutManager): androidx.recyclerview.widget.OrientationHelper {
        if (verticalHelper == null) {
            verticalHelper = androidx.recyclerview.widget.OrientationHelper.createVerticalHelper(layoutManager)
        }
        return verticalHelper!!
    }

    private fun getHorizontalHelper(layoutManager: androidx.recyclerview.widget.RecyclerView.LayoutManager): androidx.recyclerview.widget.OrientationHelper {
        if (horizontalHelper == null) {
            horizontalHelper = androidx.recyclerview.widget.OrientationHelper.createHorizontalHelper(layoutManager)
        }
        return horizontalHelper!!
    }
}
