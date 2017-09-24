package org.fossasia.susi.ai.helper

import android.text.format.DateFormat

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

/**
 * <h1>Helper class to get current date and time. Also to parse date and time from server.</h1>

 * Created by
 * --Vatsal Bajpai on
 * --05/10/16 at
 * --6:51 AM
 */
object DateTimeHelper {

    /**
     * Gets current time.

     * @return the current time
     */
    val currentTime: String
        get() {
            val delegate = "hh:mm aaa"
            return DateFormat.format(delegate, Calendar.getInstance().time) as String
        }

    /**
     * Get date string.

     * @return the string
     */
    val date: String
        get() {
            val sdf = SimpleDateFormat(" MMM dd, yyyy")
            return sdf.format(Date())
        }

    /**
     * Method to format date from server

     * @param date Date in string
     * *
     * @return Date
     */
    private fun formatDate(date: String): Date? {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        var dateIn: Date?
        try {
            dateIn = sdf.parse(date)
        } catch (e: Exception) {
            dateIn = null
        }

        return dateIn
    }

    /**
     * Get date string.

     * @param date the date
     * *
     * @return the string
     */
    fun getDate(date: String): String {
        val sdf = SimpleDateFormat(" MMM dd, yyyy")
        val tz = TimeZone.getDefault()
        sdf.timeZone = tz
        return sdf.format(formatDate(date))
    }

    /**
     * Gets time.

     * @param date the date
     * *
     * @return the time
     */
    fun getTime(date: String): String {
        val sdf = SimpleDateFormat("hh:mm aaa")
        val tz = TimeZone.getDefault()
        sdf.timeZone = tz
        return sdf.format(formatDate(date))
    }
}
