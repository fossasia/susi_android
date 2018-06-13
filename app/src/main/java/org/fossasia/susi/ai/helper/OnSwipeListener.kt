package org.fossasia.susi.ai.helper

/**
 * @author : codedsun
 * Created on 11/Jun/2018
 */
import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import timber.log.Timber
import java.lang.Math.abs

/*
 OnSwipe Listener Implemented to capture onSwipe Events and
 can be implemented within the app
 */
open class OnSwipeListener(c: Context) : View.OnTouchListener {

    var context = c;
    var gestureDetector: GestureDetector = GestureDetector(c, GestureListener())

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(p1)
    }

    open fun onSwipeLeft() {
        //will be override by the implementing class
    }

    open fun onSwipeRight() {
        //will be override by the implementing class
    }

    inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        val SWIPE_DISTANCE_THRESHOLD = 100
        val SWIPE_VELOCITY_THRESHOLD = 50

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            try {
                var distanceX = e2!!.x - e1!!.x
                var distanceY = e2.y - e1.y
                if ((abs(distanceX) > abs(distanceY)) &&
                        abs(distanceX) > SWIPE_DISTANCE_THRESHOLD &&
                        abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (distanceX > 0) onSwipeLeft()
                    else onSwipeRight()
                    return true
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
            return false
        }

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }
    }
}