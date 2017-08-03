package org.fossasia.susi.ai

import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.*
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions.*
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.MediumTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import android.view.WindowManager
import org.fossasia.susi.ai.signup.SignUpActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by mayanktripathi on 18/07/17.
 */

@RunWith(AndroidJUnit4::class)
@MediumTest
class SignUpTest {


    @Rule @JvmField
    val mActivityRule = ActivityTestRule(SignUpActivity::class.java)

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
        Log.d(SignUpTest.TAG, "running testUIViewsPresenceOnLoad..")

        // checks if email input field is present
        onView(withId(R.id.email)).check(matches(isDisplayed()))

        // checks if password input field is present
        onView(withId(R.id.password)).check(matches(isDisplayed()))

        // checks if confirm password button is present
        onView(withId(R.id.confirm_password)).check(matches(isDisplayed()))

        // checks if checkbox is present
        onView(withId(R.id.customer_server)).check(matches(isDisplayed()))

        // checks if sign up button is present
        onView(withId(R.id.sign_up)).perform(ViewActions.scrollTo())
        onView(withId(R.id.sign_up)).check(matches(isDisplayed()))

    }

    @Test
    fun testSignUp() {
        Log.d(SignUpTest.TAG, "running SignUp test..")

        val emailInput = (mActivityRule.activity.findViewById(R.id.email) as TextInputLayout).editText as TextInputEditText?
        InstrumentationRegistry.getInstrumentation().runOnMainSync { emailInput!!.setText("mayank.trp@gmail.com") }

        val passInput = (mActivityRule.activity.findViewById(R.id.password) as TextInputLayout).editText as TextInputEditText?
        InstrumentationRegistry.getInstrumentation().runOnMainSync { passInput!!.setText("abcdef") }

        val conpassInput = (mActivityRule.activity.findViewById(R.id.confirm_password) as TextInputLayout).editText as TextInputEditText?
        InstrumentationRegistry.getInstrumentation().runOnMainSync { conpassInput!!.setText("abcdef") }

        onView(withId(R.id.sign_up)).perform(ViewActions.click())
    }

    @Test
    fun testPersonalServer() {
        Log.d(SignUpTest.TAG, "running Personal Server test..")

        val emailInput = (mActivityRule.activity.findViewById(R.id.email) as TextInputLayout).editText as TextInputEditText?
        InstrumentationRegistry.getInstrumentation().runOnMainSync { emailInput!!.setText("mayank.trp@gmail.com") }

        val passInput = (mActivityRule.activity.findViewById(R.id.password) as TextInputLayout).editText as TextInputEditText?
        InstrumentationRegistry.getInstrumentation().runOnMainSync { passInput!!.setText("abcdef") }

        val conpassInput = (mActivityRule.activity.findViewById(R.id.confirm_password) as TextInputLayout).editText as TextInputEditText?
        InstrumentationRegistry.getInstrumentation().runOnMainSync { conpassInput!!.setText("abcdef") }

        onView(withId(R.id.customer_server)).perform(ViewActions.click())

        val serverInput = (mActivityRule.activity.findViewById(R.id.input_url) as TextInputLayout).editText as TextInputEditText?
        InstrumentationRegistry.getInstrumentation().runOnMainSync { serverInput!!.setText("http://104.198.32.176/") }

        onView(withId(R.id.sign_up)).perform(ViewActions.click())
    }

    companion object {
        private val TAG = "SignUpTest"
    }

}