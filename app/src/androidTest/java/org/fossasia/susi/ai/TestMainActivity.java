package org.fossasia.susi.ai;

import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.view.WindowManager;

import org.fossasia.susi.ai.activities.MainActivity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


/**
 * Created by abhinavtyagi on 09/10/16.
 */

@RunWith(AndroidJUnit4.class)
@MediumTest
public class TestMainActivity   {

    private static final String TAG = TestMainActivity.class.getSimpleName();

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void unlockScreen() {
        final MainActivity activity = mActivityRule.getActivity();
        Runnable wakeUpDevice = new Runnable() {
            public void run() {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        };
        activity.runOnUiThread(wakeUpDevice);
    }

    /**
     * Test activity_main items visibility on launch of app
     */
    @Test
    public void testUIViewsPresenceOnLoad() {
        Log.d(TAG,"running testUIViewsPresenceOnLoad..");

        // checks if recycler view is present
        onView(withId(R.id.rv_chat_feed)).check(matches(isDisplayed()));

        // checks if layout container for chat box is present
        onView(withId(R.id.send_message_layout)).check(matches(isDisplayed()));

        // checks if message box is present
        onView(withId(R.id.et_message)).check(matches(isDisplayed()));

        // checks if send button is present ... removed in revision of UI
//        onView(withId(R.id.btn_send)).check(matches(isDisplayed()));

        // checks if microphone button is present
        onView(withId(R.id.btnSpeak)).check(matches(isDisplayed()));

    }

    /**
     * Test action bar items visibility
     */
    @Test
    public void testActionBarViewsPresenceOnLoad() {
        Log.d(TAG,"running testActionBarViewsPresenceOnLoad..");

        // checks if search button is present
        onView(withId(R.id.action_search)).check(matches(isDisplayed()));

        // checks if down angle button is not present
        onView(withId(R.id.down_angle)).check(doesNotExist());

        // checks if up angle button is not present
        onView(withId(R.id.up_angle)).check(doesNotExist());

        // checks if setting menu item is not present
        onView(withId(R.id.action_settings)).check(doesNotExist());

        // checks if wallpaper setting menu item is not present
        onView(withId(R.id.wall_settings)).check(doesNotExist());

    }

}