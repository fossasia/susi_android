package org.fossasia.susi.ai.helper

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar

/**
 * <h1>Helper class for snackbar behaviour.</h1>

 * Created by rajdeep1008 on 17/10/16.
 */
class SnackbarBehavior
/**
 * Instantiates a new Snackbar behavior.

 * @param context the context
 * *
 * @param attrs the attrs
 */
(context: Context, attrs: AttributeSet) : androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior<ViewGroup>(context, attrs) {

    override fun layoutDependsOn(parent: androidx.coordinatorlayout.widget.CoordinatorLayout, child: ViewGroup, dependency: View): Boolean {
        return dependency is Snackbar.SnackbarLayout
    }

    override fun onDependentViewChanged(parent: androidx.coordinatorlayout.widget.CoordinatorLayout, child: ViewGroup, dependency: View): Boolean {
        val params = child.layoutParams as androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams
        params.bottomMargin = parent.height - dependency.y.toInt()
        child.layoutParams = params
        return true
    }

    override fun onDependentViewRemoved(parent: androidx.coordinatorlayout.widget.CoordinatorLayout, child: ViewGroup, dependency: View) {
        val params = child.layoutParams
        if (params is androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams) params.bottomMargin = 0
        child.layoutParams = params
    }
}
