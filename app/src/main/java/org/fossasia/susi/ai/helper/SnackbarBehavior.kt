package org.fossasia.susi.ai.helper

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

/**
 * <h1>Helper class for snackbar behaviour.</h1>

 * Created by rajdeep1008 on 17/10/16.
 */
class SnackbarBehavior
/**
 * Instantiates a new Snackbar behavior.

 * @param context the context
 * *
 * @param attrs   the attrs
 */
(context: Context, attrs: AttributeSet) : CoordinatorLayout.Behavior<ViewGroup>(context, attrs) {

    override fun layoutDependsOn(parent: CoordinatorLayout?, child: ViewGroup?, dependency: View?): Boolean {
        return dependency is Snackbar.SnackbarLayout
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout?, child: ViewGroup?, dependency: View?): Boolean {
        val params = child!!.layoutParams as CoordinatorLayout.LayoutParams
        params.bottomMargin = parent!!.height - dependency!!.y.toInt()
        child.layoutParams = params
        return true
    }

    override fun onDependentViewRemoved(parent: CoordinatorLayout?, child: ViewGroup?, dependency: View?) {
        val params = child!!.layoutParams as CoordinatorLayout.LayoutParams
        params.bottomMargin = 0
        child.layoutParams = params
    }
}
