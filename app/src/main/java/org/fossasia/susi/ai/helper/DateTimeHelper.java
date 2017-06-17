package org.fossasia.susi.ai.helper;

import android.text.format.DateFormat;

import java.text.ParseException;
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

    public static String getDate(String date){
        String queryDate = date.split("T")[0];
        String strDate;
        java.text.DateFormat dateFormat;
        dateFormat = java.text.DateFormat.getDateInstance(java.text.DateFormat.LONG);
        java.text.DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate;
        try {
            startDate = df.parse(queryDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        strDate = dateFormat.format(startDate);
        return strDate;
    }
}
