package org.fossasia.susi.ai.login;

import android.app.Instrumentation
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import com.squareup.haha.perflib.Queries.findObject
import android.os.Build
import android.support.test.rule.GrantPermissionRule
import android.Manifest.permission
import android.Manifest.permission.READ_PHONE_STATE
import android.support.test.InstrumentationRegistry
import android.support.test.uiautomator.UiDevice
import android.support.test.uiautomator.UiObject
import android.support.test.uiautomator.UiObjectNotFoundException
import android.support.test.uiautomator.UiSelector
import org.junit.rules.TestRule


/**
 * Created by collinx on 22-10-2017.
 */


@RunWith(AndroidJUnit4::class)
@MediumTest
class WelcomeActivityTest {
    @Rule
    @JvmField
    val mActivityRule = ActivityTestRule(WelcomeActivity::class.java)


    @Rule
    @JvmField
    val permissionRule: TestRule = GrantPermissionRule.grant(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )


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

    fun allowPermissionsIfNeeded() {

        if (Build.VERSION.SDK_INT >= 23) {
            val instr : Instrumentation  = InstrumentationRegistry.getInstrumentation()
            val mDevice = UiDevice.getInstance(instr)
            val allowPermissions : UiObject = mDevice.findObject(UiSelector().text("Allow"))
            if (allowPermissions.exists()) {
                try {
                    allowPermissions.click()
                } catch (e : UiObjectNotFoundException) {
                    Log.e(TAG, "There is no permissions dialog to interact with ", e)
                }

            }
        }
    }

    @Test
    fun testUIViewsPresenceOnLoad() {
        Log.d(TAG, "running testUIViewsPresenceOnLoad..")

        allowPermissionsIfNeeded()

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