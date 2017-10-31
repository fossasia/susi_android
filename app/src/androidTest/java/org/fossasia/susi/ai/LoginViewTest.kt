package org.fossasia.susi.ai

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
import android.support.test.rule.GrantPermissionRule
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import android.view.WindowManager
import android.widget.AutoCompleteTextView
import org.fossasia.susi.ai.login.LoginActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith

/**
 * Created by mayanktripathi on 17/07/17.
 */

@RunWith(AndroidJUnit4::class)
@MediumTest
class LoginViewTest {

    @Rule @JvmField
    val permissionRule: TestRule = GrantPermissionRule.grant(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )

    @Rule @JvmField
    val mActivityRule = ActivityTestRule(LoginActivity::class.java)

    @Before
    fun unlockScreen() {
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

        // checks if login button is present
        onView(withId(R.id.log_in)).check(matches(isDisplayed()))

        // checks if forgot password button is present
        onView(withId(R.id.forgot_password)).check(matches(isDisplayed()))

        // checks if checkbox is present
        onView(withId(R.id.customer_server)).check(matches(isDisplayed()))

        // checks if sign up button is present
        onView(withId(R.id.sign_up)).perform(scrollTo())
        onView(withId(R.id.sign_up)).check(matches(isDisplayed()))

        // checks if skip button is present
        onView(withId(R.id.skip)).perform(scrollTo())
        onView(withId(R.id.skip)).check(matches(isDisplayed()))

    }

    @Test
    @Throws(InterruptedException::class)
    fun testSignIn() {
        Log.d(TAG, "running Sign in test")
        val emailInput = (mActivityRule.activity.findViewById(R.id.email) as TextInputLayout).editText as AutoCompleteTextView?
        getInstrumentation().runOnMainSync { emailInput!!.setText("singhalsaurabh95@gmail.com") }
        val passInput = (mActivityRule.activity.findViewById(R.id.password) as TextInputLayout).editText as TextInputEditText?
        getInstrumentation().runOnMainSync { passInput!!.setText("qwertY12") }

        onView(withId(R.id.log_in)).perform(click())

        Thread.sleep(6000)
    }

    companion object {
        private val TAG = "LoginViewTest"
    }
}
