package org.fossasia.susi.ai.helper

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.TypedValue
import android.view.View


/**
 * Created by mayanktripathi on 22/06/17.
 */

class ConstraintsHelper(dimension: Int, context: Context) : RecyclerView.ItemDecoration() {

    private val space: Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dimension.toFloat(),
            context.resources.displayMetrics).toInt()

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        super.getItemOffsets(outRect, view, parent, state)
        if (parent.getChildAdapterPosition(view) == state!!.itemCount - 1 && outRect.right < space) {
            outRect.right = space
            outRect.left = 0
        }
    }

}
