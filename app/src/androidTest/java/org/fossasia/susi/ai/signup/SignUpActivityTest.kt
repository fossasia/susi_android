package org.fossasia.susi.ai.signup

import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.scrollTo
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.filters.MediumTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import android.view.WindowManager
import org.fossasia.susi.ai.R
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
class SignUpActivityTest {


    @Rule
    @JvmField
    val mActivityRule = ActivityTestRule(SignUpActivity::class.java)

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

        // checks if email input field is present
        onView(withId(R.id.email)).check(matches(isDisplayed()))

        // checks if password input field is present
        onView(withId(R.id.password)).check(matches(isDisplayed()))


        // checks if confirm password button is present
        onView(withId(R.id.confirm_password)).check(matches(isDisplayed()))

        // checks if checkbox is present
        onView(withId(R.id.customer_server)).check(matches(isDisplayed()))

        // checks if sign up button is present
        onView(withId(R.id.sign_up)).perform(scrollTo())
        onView(withId(R.id.sign_up)).check(matches(isDisplayed()))

    }

    companion object {
        private val TAG = "SignUpActivityTest"
    }

}