package org.fossasia.susi.ai.helper;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by saurabh on 8/10/16.
 */

public class AndroidHelper {
    public static final String GOOGLE_MAPS_PKG = "com.google.android.apps.maps";

    public static boolean isGoogleMapsInstalled(Context context) {
        return isAppInstalled(context, GOOGLE_MAPS_PKG);
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
