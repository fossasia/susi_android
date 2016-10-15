package org.fossasia.susi.ai;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.test.annotation.UiThreadTest;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.fossasia.susi.ai.activities.LoginActivity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
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

    @UiThreadTest
    @Before
    public void SetUp() throws Throwable {
        TestUtils.unlockScreen(mActivityRule);
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


        // checks if sign up button is present
        onView(withId(R.id.sign_up)).check(matches(isDisplayed()));

    }

    /**
     * Test Sign in for app.
     */
    @Test
    public void testSignIn() throws InterruptedException {
        Log.d(TAG, "running Sign in test");
        final TextInputEditText emailInput = (TextInputEditText) ((TextInputLayout) mActivityRule.getActivity().findViewById(R.id.email)).getEditText();
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

        onView(withId(R.id.log_in)).perform(click());

        Thread.sleep(3000);


    }
}
