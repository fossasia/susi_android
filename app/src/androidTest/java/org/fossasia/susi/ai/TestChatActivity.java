package org.fossasia.susi.ai;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.view.WindowManager;

import org.fossasia.susi.ai.chat.ChatActivity;
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
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * <h1>Unit Test for testing main activity.</h1>
 *
 * Created by abhinavtyagi on 09/10/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestChatActivity {

    private static final String TAG = TestChatActivity.class.getSimpleName();

    @Rule
    public ActivityTestRule<ChatActivity> mActivityRule = new ActivityTestRule<>(ChatActivity.class);

    /**
     * Unlock screen.
     *
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     */
    @Before
    public void unlockScreen() throws IOException, InterruptedException {
        Log.d(TAG,"running unlockScreen..");

        final ChatActivity activity = mActivityRule.getActivity();
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
     * Test activity_chat items visibility on launch of app
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
     * Test action bar items visibility on Search click
     */
    @Test
    public void test04_SearchButtonClickUIChanges() {
        Log.d(TAG,"running test04_SearchButtonClickUIChanges..");

        // enter text to chat
        onView(withId(R.id.et_message)).perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.et_message)).perform(click()).perform(typeText("Hi! I am Unit Test"));

        // click send button
        onView(withId(R.id.btnSpeak)).perform(click());

    }
}