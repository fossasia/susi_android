package org.fossasia.susi.ai;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.RadioButton;

import org.fossasia.susi.ai.activities.LoginActivity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by saurabh on 13/10/16.
 */
@RunWith(AndroidJUnit4.class)
@MediumTest
public class TestLoginActivity {
    public static final String TAG = TestLoginActivity.class.getSimpleName();

    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(LoginActivity.class);

    @Before
    public void unlockScreen() {
        final LoginActivity activity = mActivityRule.getActivity();
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
     * Test activity_login items visibility on launch of app
     */
    @Test
    public void testUIViewsPresenceOnLoad() {
        Log.d(TAG, "running testUIViewsPresenceOnLoad..");

        // checks if email input field is present
        onView(withId(R.id.email)).check(matches(isDisplayed()));

        // checks if password input field is present
        onView(withId(R.id.password)).check(matches(isDisplayed()));

        // checks if login button is present
        onView(withId(R.id.log_in)).check(matches(isDisplayed()));

        // checks if radio buttons are present
        onView(withId(R.id.susi_default)).check(matches(isDisplayed()));
        onView(withId(R.id.personal_server)).check(matches(isDisplayed()));

        // checks if sign up button is present
        onView(withId(R.id.sign_up)).perform(scrollTo());
        onView(withId(R.id.sign_up)).check(matches(isDisplayed()));

    }

    /**
     * Test Sign in for app.
     */
    @Test
    public void testSignIn() throws InterruptedException {
        Log.d(TAG, "running Sign in test");
        final AutoCompleteTextView emailInput = (AutoCompleteTextView) ((TextInputLayout) mActivityRule.getActivity().findViewById(R.id.email)).getEditText();
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                emailInput.setText("singhalsaurabh95@gmail.com");
            }
        });
        final TextInputEditText passInput = (TextInputEditText) ((TextInputLayout) mActivityRule.getActivity().findViewById(R.id.password)).getEditText();
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                passInput.setText("qwertY12");
            }
        });

        final RadioButton susiServer = (RadioButton) ((RadioButton) mActivityRule.getActivity().findViewById(R.id.susi_default));
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                susiServer.setChecked(true);
            }
        });

        onView(withId(R.id.log_in)).perform(click());

        Thread.sleep(3000);
    }
}
