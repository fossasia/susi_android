package org.fossasia.susi.ai;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.util.Log;
import android.view.WindowManager;

import static android.content.Context.KEYGUARD_SERVICE;

/**
 * Created by saurabh on 15/10/16.
 */

public class TestUtils {
    public static final String TAG = TestUtils.class.getSimpleName();

    /**
     * Unlocks Screen before starting test.
     */
    public static void unlockScreen(ActivityTestRule mActivityRule) throws Throwable {
        Log.d(TAG, "Running unlockScreen");
        final Activity activity = mActivityRule.getActivity();
        mActivityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                KeyguardManager mKG = (KeyguardManager) activity.getSystemService(Context.KEYGUARD_SERVICE);
                KeyguardManager.KeyguardLock mLock = mKG.newKeyguardLock(KEYGUARD_SERVICE);
                mLock.disableKeyguard();

                //turn the screen on
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
            }
        });
    }
}
