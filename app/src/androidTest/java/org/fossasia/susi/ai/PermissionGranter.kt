package org.fossasia.susi.ai

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.uiautomator.UiDevice
import android.support.test.uiautomator.UiObject
import android.support.test.uiautomator.UiSelector
import android.support.test.InstrumentationRegistry
import android.support.test.uiautomator.UiObjectNotFoundException
import android.support.v4.content.ContextCompat;

class PermissionGranter {

    companion object {
        val PERMISSIONS_DIALOG_DELAY: Long = 3000
        val GRANT_BUTTON_INDEX: Int = 2
        fun allowPermissionsIfNeeded(permissionNeeded: String) {
            try {
                if (Build.VERSION.SDK_INT >= 23 && !hasNeededPermission(permissionNeeded)) {
                    sleep(PERMISSIONS_DIALOG_DELAY)
                    var device = UiDevice.getInstance(getInstrumentation())
                    var allowPermissions: UiObject = device.findObject(UiSelector()
                            .clickable(true)
                            .checkable(false)
                            .index(GRANT_BUTTON_INDEX))
                    if (allowPermissions.exists()) {
                        allowPermissions.click()
                    }
                }
            } catch (e: UiObjectNotFoundException) {
                System.out.println("There is no permissions dialog to interact with");
            }
        }


        fun hasNeededPermission(permissionNeeded: String): Boolean {
            var context: Context = InstrumentationRegistry.getTargetContext()
            var permissionStatus: Int = ContextCompat.checkSelfPermission(context, permissionNeeded)
            if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                return true
            } else {
                return false
            }
        }

        fun sleep(millis: Long) {
            try {
                Thread.sleep(millis);
            } catch (e: InterruptedException) {
                throw RuntimeException("Cannot execute Thread.sleep()");
            }
        }
    }
}