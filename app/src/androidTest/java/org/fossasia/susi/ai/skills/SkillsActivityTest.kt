package org.fossasia.susi.ai.skills

import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.filters.LargeTest
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
 * Created by collinx on 16-10-2017.
 */

@RunWith(AndroidJUnit4::class)
@LargeTest
class SkillsActivityTest {


    @Rule
    @JvmField
    val mActivityRule = ActivityTestRule(SkillsActivity::class.java)

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
    fun testUIViews() {
        Log.d(TAG, "running testUIViews..")

        // checks if frame layout is displayed or not
        onView(withId(R.id.fragment_container)).check(matches(isDisplayed()))
    }

    companion object {
        private val TAG = "SkillsActivityTest"
    }
}