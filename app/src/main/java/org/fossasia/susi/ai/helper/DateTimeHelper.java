package org.fossasia.susi.ai.helper;

import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by
 * --Vatsal Bajpai on
 * --05/10/16 at
 * --6:51 AM
 */

public class DateTimeHelper {

    public static String getCurrentTime() {
        String delegate = "hh:mm aaa";
        return (String) DateFormat.format(delegate,Calendar.getInstance().getTime());
    }

    public static String getDate(){
        SimpleDateFormat sdf = new SimpleDateFormat(" MMM dd, yyyy");
        return sdf.format(new Date());
    }
}
