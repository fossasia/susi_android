package org.fossasia.susi.ai.skills

import android.content.Context
import android.graphics.PointF
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.RecyclerView


/**
 * Custom LayoutManager to smoothly scroll the list to the top when the user hits
 * the back space and the results changes by a great amount
 */
class LayoutManagerSmoothScroller(context: Context) : LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) {


    /**
     *  smoothly scrolls the result of the list to the top
     * @param recyclerView [RecyclerView] on which scroll is to happen
     * @param state [RecyclerView.State] same as super method
     * @param position position to which the scroll is to be applied
     */
    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State?, position: Int) {
        val smoothScroller = TopSnappedSmoothScroller(recyclerView.context)
        smoothScroller.targetPosition = position
        startSmoothScroll(smoothScroller)
    }

    private inner class TopSnappedSmoothScroller(context: Context) : LinearSmoothScroller(context) {

        override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
            return this@LayoutManagerSmoothScroller
                    .computeScrollVectorForPosition(targetPosition)
        }

        override fun getVerticalSnapPreference(): Int {
            return LinearSmoothScroller.SNAP_TO_START
        }
    }
}