package org.fossasia.susi.ai.login;

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.filters.MediumTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import android.view.WindowManager
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.ChatActivityTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


/**
 * Created by collinx on 22-10-2017.
 */


@RunWith(AndroidJUnit4::class)
@MediumTest
class WelcomeActivityTest {
    @Rule
    @JvmField
    val mActivityRule = ActivityTestRule(WelcomeActivity::class.java)

    @Before
    @Throws(IOException::class, InterruptedException::class)
    fun unlockScreen() {
        Log.d(TAG, "running unlockScreen..")

        val activity = mActivityRule.activity
        val wakeUpDevice = Runnable {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        activity.runOnUiThread(wakeUpDevice)
    }

    @Test
    fun testUIViewsPresenceOnLoad() {
        Log.d(TAG, "running testUIViewsPresenceOnLoad..")

        // checks if viewpager is displayed or not
        onView(withId(R.id.pager)).check(matches(isDisplayed()))

        // checks if tab layout is shown or not
        onView(withId(R.id.tabDots)).check(matches(isDisplayed()))

        // checks if next button is present
        onView(withId(R.id.btn_next)).check(matches(isDisplayed()))

        // checks if skip button is present
        onView(withId(R.id.btn_skip)).check(matches(isDisplayed()))

    }

    companion object {
        private val TAG = "WelcomeActivityTest"
    }
}