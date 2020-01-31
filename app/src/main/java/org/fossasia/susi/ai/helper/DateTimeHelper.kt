package org.fossasia.susi.ai.helper

import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import timber.log.Timber

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
            return DateFormat.format(delegate, Calendar.getInstance().time).toString()
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

    val today: Date
        get() {
            return Calendar.getInstance().time
        }

    /**
     * Method to format date from server

     * @param date Date in string
     * *
     * @return Date
     */
    private fun formatDate(date: String): Date {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        var dateIn: Date?
        dateIn = try {
            sdf.parse(date)
        } catch (e: Exception) {
            Timber.e(e)
            today
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
        return if (date.isEmpty()) {
            throw IllegalArgumentException("date argument is empty")
        } else {
            sdf.format(formatDate(date))
        }
    }

    fun formatDate(timestamp: String, months: Array<String>): String {
        return if (timestamp.length > 10 && months.size == 12) {
            var date: String? = ""
            timestamp.trim()
            val month = timestamp.substring(5, 7).toInt()
            date = timestamp.substring(8, 10) + " " +
                    months[month - 1].toString() +
                    ", " + timestamp.substring(0, 4)

            date
        } else {
            throw IllegalArgumentException("Timestamp or Months format not correct")
        }
    }
}
