package org.fossasia.susi.ai.helper

import android.content.Context
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.fossasia.susi.ai.R

class SimpleDividerItemDecoration(context: Context, private val drawDividerFromIndex: Int) : androidx.recyclerview.widget.RecyclerView.ItemDecoration() {
    private val divider = ContextCompat.getDrawable(context, R.drawable.rv_divider)

    override fun onDrawOver(canvas: Canvas, parent: androidx.recyclerview.widget.RecyclerView, state: androidx.recyclerview.widget.RecyclerView.State) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        val childCount = parent.childCount
        for (i in drawDividerFromIndex until childCount) {
            val child = parent.getChildAt(i)

            val params = child.layoutParams as androidx.recyclerview.widget.RecyclerView.LayoutParams

            val top = child.bottom + params.bottomMargin
            val bottom = top + (divider?.intrinsicHeight ?: 0)

            divider?.setBounds(left, top, right, bottom)
            divider?.draw(canvas)
        }
    }
}
