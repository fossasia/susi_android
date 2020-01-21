package org.fossasia.susi.ai.chat

import android.Manifest
import android.view.WindowManager
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import androidx.test.runner.AndroidJUnit4
import java.io.IOException
import org.fossasia.susi.ai.R
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import timber.log.Timber

/**
 * Created by collinx on 22-10-2017.
 */

@Suppress("DEPRECATION")
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
     * @throws IOException the io exception
     * @throws InterruptedException the interrupted exception
     */
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

    /**
     * Test activity_chat items visibility on launch of app
     */
    @Test
    fun test01_UIViewsPresenceOnLoad() {
        Timber.d("running test01_UIViewsPresenceOnLoad..")

        // checks if recycler view is present
        onView(withId(R.id.rv_chat_feed)).check(matches(isDisplayed()))

        // checks if layout container for chat box is present
        onView(withId(R.id.sendMessageLayout)).check(matches(isDisplayed()))

        // checks if message box is present
        onView(withId(R.id.askSusiMessage)).check(matches(isDisplayed()))

        // checks if microphone button is present
        onView(withId(R.id.btnSpeak)).check(matches(isDisplayed()))
    }
}
