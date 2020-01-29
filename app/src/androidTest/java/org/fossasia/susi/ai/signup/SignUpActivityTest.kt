package org.fossasia.susi.ai.signup

import android.view.WindowManager
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.ActivityTestRule
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

@RunWith(AndroidJUnit4ClassRunner::class)
@MediumTest
class SignUpActivityTest {

    @Rule
    @JvmField
    val mActivityRule = ActivityTestRule(SignUpActivity::class.java)

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

        // checks if email input field is present
        onView(withId(R.id.email)).check(matches(isDisplayed()))

        // checks if password input field is present
        onView(withId(R.id.password)).check(matches(isDisplayed()))

        // checks if confirm password button is present
        onView(withId(R.id.confirmPassword)).check(matches(isDisplayed()))

        // checks if checkbox is present
        onView(withId(R.id.customServerSignUp)).check(matches(isDisplayed()))

        // checks if sign up button is present
        onView(withId(R.id.signUp)).perform(scrollTo())
        onView(withId(R.id.signUp)).check(matches(isDisplayed()))
    }
}
