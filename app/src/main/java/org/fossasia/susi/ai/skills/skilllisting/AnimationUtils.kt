package org.fossasia.susi.ai.skills.skilllisting

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.view.View

/**
 * Animation Utils class helps to animate the views
 */
object AnimationUtils {

    /**
     * Animate the view
     *
     * @param view view that will be animated
     * @param enterOrExit true to enter, false to exit
     * @param duration how long the animation will take, in milliseconds
     * @param delay how long the animation will wait to start, in milliseconds
     * @param execOnEnd runnable that will be executed when the animation ends
     */
    @JvmOverloads
    fun animateView(view: View, enterOrExit: Boolean, duration: Long, delay: Long = 0, execOnEnd: Runnable? = null) {

        if (view.visibility == View.VISIBLE && enterOrExit) {
            view.animate().setListener(null).cancel()
            view.visibility = View.VISIBLE
            view.alpha = 1f
            execOnEnd?.run()
            return
        } else if ((view.visibility == View.GONE || view.visibility == View.INVISIBLE) && !enterOrExit) {
            view.animate().setListener(null).cancel()
            view.visibility = View.GONE
            view.alpha = 0f
            execOnEnd?.run()
            return
        }

        view.animate().setListener(null).cancel()
        view.visibility = View.VISIBLE

        animateLightSlideAndAlpha(view, enterOrExit, duration, delay, execOnEnd)
    }

    private fun animateLightSlideAndAlpha(view: View, enterOrExit: Boolean, duration: Long, delay: Long, execOnEnd: Runnable?) {
        if (enterOrExit) {
            view.translationY = (-view.height / 2).toFloat()
            view.alpha = 0f
            view.animate().setInterpolator(FastOutSlowInInterpolator()).alpha(1f).translationY(0f)
                    .setDuration(duration).setStartDelay(delay).setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            execOnEnd?.run()
                        }
                    }).start()
        } else {
            view.animate().setInterpolator(FastOutSlowInInterpolator()).alpha(0f).translationY((-view.height / 2).toFloat())
                    .setDuration(duration).setStartDelay(delay).setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            view.visibility = View.GONE
                            execOnEnd?.run()
                        }
                    }).start()
        }
    }
}
