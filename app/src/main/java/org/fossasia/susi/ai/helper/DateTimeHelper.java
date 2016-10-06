package org.fossasia.susi.ai.helper;

import java.util.Calendar;

/**
 * Created by
 * --Vatsal Bajpai on
 * --05/10/16 at
 * --6:51 AM
 */

public class DateTimeHelper {

    public static String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        return String.format("%02d", calendar.get(Calendar.HOUR)) + ":" +
                String.format("%02d", calendar.get(Calendar.MINUTE));
    }
}
