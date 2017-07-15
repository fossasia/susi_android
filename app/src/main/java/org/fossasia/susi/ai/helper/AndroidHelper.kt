package org.fossasia.susi.ai.helper

import android.content.Context
import android.content.pm.PackageManager

/**
 * <h1>Helper class to check if google maps is installed.</h1>

 * Created by saurabh on 8/10/16.
 */
object AndroidHelper {

    val GOOGLE_MAPS_PKG = "com.google.android.apps.maps"

    /**
     * Is google maps installed boolean.

     * @param context the context
     * *
     * @return the boolean
     */
    fun isGoogleMapsInstalled(context: Context): Boolean {
        return isAppInstalled(context, GOOGLE_MAPS_PKG)
    }

    private fun isAppInstalled(context: Context, packageName: String): Boolean {
        try {
            context.packageManager.getApplicationInfo(packageName, 0)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            return false
        }

    }
}
