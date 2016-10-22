package org.fossasia.susi.ai.test;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.activities.MainActivity;

/**
 * Created by
 * --Vatsal Bajpai on
 * --30/09/16 at
 * --10:56 AM
 */

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    MainActivity testMainActivity;
    CoordinatorLayout coordinatorLayout;
    EditText etMessage;
    LinearLayout sendMessageLayout;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        testMainActivity = getActivity();
        etMessage = (EditText) testMainActivity.findViewById(R.id.et_message);
        sendMessageLayout = (LinearLayout) testMainActivity.findViewById(R.id.send_message_layout);
        coordinatorLayout = (CoordinatorLayout) testMainActivity.findViewById(R.id.coordinator_layout);
    }

    /**
     * Test if your test fixture has been set up correctly.
     * You should always implement a test that
     * checks the correct setup of your test fixture.
     * If this tests fails all other tests are
     * likely to fail as well.
     */

    public void testPreconditions() throws Exception {
        assertNotNull("testMainActivity is null", testMainActivity);
        assertNotNull("etMessage is null", etMessage);
        assertNotNull("sendMessageLayout is null", sendMessageLayout);
        assertNotNull("coordinatorLayout is null", coordinatorLayout);
    }

    public void testEmptyEditTextMessage() throws Exception {
        boolean actualB = etMessage.getText().toString().trim().isEmpty();
        assertNotSame("etMessage should not be empty", false, actualB);
    }
}
