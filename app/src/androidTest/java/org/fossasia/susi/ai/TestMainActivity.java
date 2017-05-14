package org.fossasia.susi.ai;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

import org.fossasia.susi.ai.activities.MainActivity;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.IOException;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressKey;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by abhinavtyagi on 09/10/16.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestMainActivity {

    private static final String TAG = TestMainActivity.class.getSimpleName();

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void unlockScreen() throws IOException, InterruptedException {
        Log.d(TAG,"running unlockScreen..");

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
    public void test01_UIViewsPresenceOnLoad() {
        Log.d(TAG,"running test01_UIViewsPresenceOnLoad..");

        // checks if recycler view is present
        onView(withId(R.id.rv_chat_feed)).check(matches(isDisplayed()));

        // checks if layout container for chat box is present
        onView(withId(R.id.send_message_layout)).check(matches(isDisplayed()));

        // checks if message box is present
        onView(withId(R.id.et_message)).check(matches(isDisplayed()));

        // checks if microphone button is present
        onView(withId(R.id.btnSpeak)).check(matches(isDisplayed()));
    }

    /**
     * Test action bar items visibility
     */
    @Test
    public void test02_ActionBarViewsPresenceOnLoad() {
        Log.d(TAG,"running test02_ActionBarViewsPresenceOnLoad..");

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

        // checks if log out setting menu item is not present
        onView(withId(R.id.action_logout)).check(doesNotExist());
    }

    /**
     * Test overflow items visibility on overflow icon click
     */
    @Test
    public void test03_OverflowMenuItemsPresenceOnIconClick() {
        Log.d(TAG,"running test03_OverflowMenuItemsPresenceOnIconClick..");

        // open overflow menu
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        // checks if setting menu item is present
        onView(withText(R.string.action_settings)).check(matches(isDisplayed()));

        // checks if wallpaper setting menu item is present
        onView(withText(R.string.action_wall_settings)).check(matches(isDisplayed()));

        // checks if log out setting menu item is present
        onView(withText(R.string.action_log_out)).check(matches(isDisplayed()));
    }

    /**
     * Test action bar items visibility on Search click
     */
    @Test
    public void test04_SearchButtonClickUIChanges() {
        Log.d(TAG,"running test04_SearchButtonClickUIChanges..");

        // enter text to chat
        onView(withId(R.id.et_message)).perform(click()).perform(typeText("Hi! I am Unit Test"));

        // click send button
        onView(withId(R.id.btnSpeak)).perform(click());

        // checks if search button is present
        onView(withId(R.id.action_search)).check(matches(isDisplayed()));

        // get the fab button and perform click operation
        onView(withId(R.id.action_search)).perform(click());

        // enter text to search
        onView(withId(android.support.design.R.id.search_src_text)).perform(typeText("Uni"), pressKey(KeyEvent.KEYCODE_ENTER));

        // checks if down angle button is present
        onView(withId(R.id.down_angle)).check(matches(isDisplayed()));

        // checks if up angle button is present
        onView(withId(R.id.up_angle)).check(matches(isDisplayed()));

        // checks if setting menu item is not present
        onView(withId(R.id.action_settings)).check(doesNotExist());

        // checks if wallpaper setting menu item is not present
        onView(withId(R.id.wall_settings)).check(doesNotExist());

        // checks if log out setting menu item is not present
        onView(withId(R.id.action_logout)).check(doesNotExist());
    }
}