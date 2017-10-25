package org.fossasia.susi.ai.test;

import android.support.annotation.VisibleForTesting;
import android.support.design.widget.CoordinatorLayout;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.chat.ChatActivity;

/**
 * <h1>Unit Test for testing fixtures in main activity.</h1>
 *
 * Created by
 * --Vatsal Bajpai on
 * --30/09/16 at
 * --10:56 AM
 */
public class ChatActivityTest extends ActivityInstrumentationTestCase2<ChatActivity> {

    @VisibleForTesting
    ChatActivity testChatActivity;
    CoordinatorLayout coordinatorLayout;
    EditText etMessage;
    LinearLayout sendMessageLayout;

    /**
     * Instantiates a new Main activity test.
     */
    public ChatActivityTest() {
        super(ChatActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        testChatActivity = getActivity();
        etMessage = (EditText) testChatActivity.findViewById(R.id.et_message);
        sendMessageLayout = (LinearLayout) testChatActivity.findViewById(R.id.send_message_layout);
        coordinatorLayout = (CoordinatorLayout) testChatActivity.findViewById(R.id.coordinator_layout);
    }

    /**
     * Test if your test fixture has been set up correctly.
     * You should always implement a test that
     * checks the correct setup of your test fixture.
     * If this tests fails all other tests are
     * likely to fail as well.
     *
     * @throws Exception the exception
     */
    public void testPreconditions() throws Exception {
        assertNotNull("testMainActivity is null", testChatActivity);
        assertNotNull("etMessage is null", etMessage);
        assertNotNull("sendMessageLayout is null", sendMessageLayout);
        assertNotNull("coordinatorLayout is null", coordinatorLayout);
    }

    /**
     * Test empty edit text message.
     *
     * @throws Exception the exception
     */
    public void testEmptyEditTextMessage() throws Exception {
        boolean actualB = etMessage.getText().toString().trim().isEmpty();
        assertNotSame("etMessage should not be empty", false, actualB);
    }
}
