package org.fossasia.susi.ai.helper

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone
import junit.framework.TestCase.assertEquals
import org.junit.Test

class DateTimeHelperTest {
    private val TIMEZONE_SINGAPORE = "Asia/Singapore"
    private val TIMEZONE_US_PACIFIC = "US/Pacific"
    private val TIMEZONE_SYDNEY = "Australia/Sydney"
    private val TIMEZONE_AMSTERDAM = "Amsterdam"
    private val TEST_TIME = "2020-01-30T12:18:37.262Z"

    val months = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Aug", "Nov", "Dec")
    val emptyMonths = arrayOf("")

    @Test
    fun getDateTest() {
        val sdf = SimpleDateFormat(" MMM dd, yyyy")
        assertEquals(sdf.format(Calendar.getInstance().time), DateTimeHelper.getDate(""))

        TimeZone.setDefault(TimeZone.getTimeZone(TIMEZONE_SINGAPORE))
        assertEquals(" Jan 30, 2020", DateTimeHelper.getDate(TEST_TIME))

        TimeZone.setDefault(TimeZone.getTimeZone(TIMEZONE_US_PACIFIC))
        assertEquals(" Jan 30, 2020", DateTimeHelper.getDate(TEST_TIME))

        TimeZone.setDefault(TimeZone.getTimeZone(TIMEZONE_SYDNEY))
        assertEquals(" Jan 30, 2020", DateTimeHelper.getDate(TEST_TIME))

        TimeZone.setDefault(TimeZone.getTimeZone(TIMEZONE_AMSTERDAM))
        assertEquals(" Jan 30, 2020", DateTimeHelper.getDate(TEST_TIME))
    }

    @Test
    fun getTimeTest() {
        TimeZone.setDefault(TimeZone.getTimeZone(TIMEZONE_SINGAPORE))
        assertEquals("08:18 PM", DateTimeHelper.getTime(TEST_TIME))

        TimeZone.setDefault(TimeZone.getTimeZone(TIMEZONE_US_PACIFIC))
        assertEquals("04:18 AM", DateTimeHelper.getTime(TEST_TIME))

        TimeZone.setDefault(TimeZone.getTimeZone(TIMEZONE_SYDNEY))
        assertEquals("11:18 PM", DateTimeHelper.getTime(TEST_TIME))

        TimeZone.setDefault(TimeZone.getTimeZone(TIMEZONE_AMSTERDAM))
        assertEquals("12:18 PM", DateTimeHelper.getTime(TEST_TIME))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testEmptyDate_in_getTime() {
        DateTimeHelper.getTime("")
    }

    @Test
    fun formatDateTest() {
        assertEquals("30 Jan, 2020", DateTimeHelper.formatDate(TEST_TIME, months))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testEmptyTimestamp_in_formatDateTest() {
        DateTimeHelper.formatDate("", months)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testEmptyMonths_in_formatDateTest() {
        DateTimeHelper.formatDate("", emptyMonths)
    }
}
