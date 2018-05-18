package org.fossasia.susi.ai.chat

import android.Manifest
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.rule.GrantPermissionRule
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import android.view.WindowManager
import org.fossasia.susi.ai.R
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.io.IOException

/**
 * Created by collinx on 22-10-2017.
 */

@RunWith(AndroidJUnit4::class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ChatActivityTest {

    @Rule
    @JvmField
    val permissionRule: TestRule = GrantPermissionRule.grant(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
                )


    @Rule
    @JvmField
     val mActivityRule = ActivityTestRule(ChatActivity::class.java)

    /**
     * Unlock screen.
     *
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     */
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

    /**
     * Test activity_chat items visibility on launch of app
     */
    @Test
    fun test01_UIViewsPresenceOnLoad() {
        Log.d(TAG, "running test01_UIViewsPresenceOnLoad..")

        // checks if recycler view is present
        onView(withId(R.id.rv_chat_feed)).check(matches(isDisplayed()))

        // checks if layout container for chat box is present
        onView(withId(R.id.send_message_layout)).check(matches(isDisplayed()))

        // checks if message box is present
        onView(withId(R.id.et_message)).check(matches(isDisplayed()))

        // checks if microphone button is present
        onView(withId(R.id.btnSpeak)).check(matches(isDisplayed()))
    }

    companion object {
        private val TAG = "ChatActivityTest"
    }
}