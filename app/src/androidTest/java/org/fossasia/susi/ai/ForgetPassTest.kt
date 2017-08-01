package org.fossasia.susi.ai

import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.*
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.MediumTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import android.view.WindowManager
import org.fossasia.susi.ai.forgotPassword.ForgotPasswordActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by mayanktripathi on 18/07/17.
 */

@RunWith(AndroidJUnit4::class)
@MediumTest
class ForgetPassTest {


    @Rule @JvmField
    val mActivityRule = ActivityTestRule(ForgotPasswordActivity::class.java)

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
        Log.d(ForgetPassTest.TAG, "running testUIViewsPresenceOnLoad..")

        // checks if email input field is present
        onView(withId(R.id.forgot_email)).check(ViewAssertions.matches(isDisplayed()))

        // checks if reset button is present
        onView(withId(R.id.reset_button)).check(ViewAssertions.matches(isDisplayed()))

        //checks if checkbox present
        onView(withId(R.id.customer_server)).check(ViewAssertions.matches(isDisplayed()))

    }

    @Test
    fun testReset() {
        Log.d(ForgetPassTest.TAG, "running resetPassword test..")

        val emailInput = (mActivityRule.activity.findViewById(R.id.forgot_email) as TextInputLayout).editText as TextInputEditText?
        InstrumentationRegistry.getInstrumentation().runOnMainSync { emailInput!!.setText("mayank.trp@gmail.com") }

        onView(withId(R.id.reset_button)).perform(click())

    }

    @Test
    fun testPersonalServer() {
        Log.d(ForgetPassTest.TAG, "running resetPassword test on personal server..")

        val emailInput = (mActivityRule.activity.findViewById(R.id.forgot_email) as TextInputLayout).editText as TextInputEditText?
        InstrumentationRegistry.getInstrumentation().runOnMainSync { emailInput!!.setText("mayank.trp@gmail.com") }

        onView(withId(R.id.customer_server)).perform(click())

        val serverInput = (mActivityRule.activity.findViewById(R.id.input_url) as TextInputLayout).editText as TextInputEditText?
        InstrumentationRegistry.getInstrumentation().runOnMainSync { serverInput!!.setText("http://104.198.32.176/") }

        onView(withId(R.id.reset_button)).perform(click())

    }

    companion object {
        private val TAG = "ForgetPassTest"
    }
}
