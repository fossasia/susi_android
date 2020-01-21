package org.fossasia.susi.ai.login

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import androidx.test.rule.ActivityTestRule
import android.view.WindowManager
import androidx.test.runner.AndroidJUnit4
import java.io.IOException
import org.fossasia.susi.ai.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber

/**
 * Created by collinx on 22-10-2017.
 */

@Suppress("DEPRECATION")
@RunWith(AndroidJUnit4::class)
@MediumTest
class WelcomeActivityTest {
    @Rule
    @JvmField
    val mActivityRule = ActivityTestRule(WelcomeActivity::class.java)

    @Before
    @Throws(IOException::class, InterruptedException::class)
    fun unlockScreen() {
        Timber.d("running unlockScreen..")

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
        Timber.d("running testUIViewsPresenceOnLoad..")

        // checks if viewpager is displayed or not
        onView(withId(R.id.pager)).check(matches(isDisplayed()))

        // checks if tab layout is shown or not
        onView(withId(R.id.tabDots)).check(matches(isDisplayed()))

        // checks if next button is present
        onView(withId(R.id.btn_next)).check(matches(isDisplayed()))

        // checks if skip button is present
        onView(withId(R.id.btn_skip)).check(matches(isDisplayed()))
    }
}
