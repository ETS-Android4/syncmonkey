package com.chesapeaketechnology.syncmonkey;

import androidx.core.util.Pair;

import org.junit.Test;

import java.time.Month;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;

/**
 * Tests for main activity
 */
public class SyncMonkeyUtilsTest
{
    @Test
    public void urlExpirationMessageIsCorrect()
    {
        final ZonedDateTime now = ZonedDateTime.now();
        final ZonedDateTime tenDaysAgo = now.minusDays(10);
        final ZonedDateTime yesterday = now.minusDays(1);
        final ZonedDateTime earlierToday = now.minusSeconds(1); // see [1]
        final ZonedDateTime laterToday = now.plusSeconds(1); // see [1]
        final ZonedDateTime tomorrow = now.plusDays(1);
        final ZonedDateTime tenDaysFromNow = now.plusDays(10);

        // When URL is valid and expires in x days
        Pair<Boolean, String> result1 = SyncMonkeyUtils.getUrlExpirationMessage(now, tenDaysAgo, laterToday);
        assertEquals(true, result1.first);
        assertEquals("SAS URL expires today", result1.second);

        Pair<Boolean, String> result2 = SyncMonkeyUtils.getUrlExpirationMessage(now, tenDaysAgo, tomorrow);
        assertEquals(true, result2.first);
        assertEquals("SAS URL expires tomorrow", result2.second);

        Pair<Boolean, String> result3 = SyncMonkeyUtils.getUrlExpirationMessage(now, tenDaysAgo, tenDaysFromNow);
        assertEquals(true, result3.first);
        assertEquals("SAS URL expires in 10 days", result3.second);

        // When URL is not yet valid
        Pair<Boolean, String> result4 = SyncMonkeyUtils.getUrlExpirationMessage(tenDaysAgo, now, tenDaysFromNow);
        assertEquals(false, result4.first);
        assertEquals("SAS URL not valid until 10 days from now", result4.second);

        Pair<Boolean, String> result5 = SyncMonkeyUtils.getUrlExpirationMessage(yesterday, now, tenDaysFromNow);
        assertEquals(false, result5.first);
        assertEquals("SAS URL not valid until 1 day from now", result5.second);

        Pair<Boolean, String> result6 = SyncMonkeyUtils.getUrlExpirationMessage(earlierToday, now, tenDaysFromNow);
        assertEquals(false, result6.first);
        assertEquals("SAS URL not yet within valid date range", result6.second);

        // When URL is expired
        Pair<Boolean, String> result7 = SyncMonkeyUtils.getUrlExpirationMessage(now, tenDaysAgo, yesterday);
        assertEquals(false, result7.first);
        assertEquals("SAS URL is expired", result7.second);

        // [1] These will technically fail if you run them at exactly one second
        // before or after midnight. If that is the case, close your IDE and go to sleep.
    }

    @Test
    public void dateTimeZoneTest()
    {
        final ZonedDateTime sasUrlStartDateTime = SyncMonkeyUtils.parseSasUrlDate("2020-10-31T12:07:25Z");
        final ZonedDateTime sasUrlEndDateTime = sasUrlStartDateTime.plusMonths(1);

        final ZonedDateTime localDateTime = ZonedDateTime.parse("2020-10-31T10:15:30-04:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        Pair<Boolean, String> result3 = SyncMonkeyUtils.getUrlExpirationMessage(localDateTime, sasUrlStartDateTime, sasUrlEndDateTime);
        assertEquals(true, result3.first);
        assertEquals("SAS URL expires in 29 days", result3.second);
    }

    @Test
    public void sasUrlDatestringsParseCorrectly()
    {
        final ZonedDateTime date1 = SyncMonkeyUtils.parseSasUrlDate("2020-10-31T12:07:25Z");
        final ZonedDateTime date2 = SyncMonkeyUtils.parseSasUrlDate("2020-10-31T12:07Z");
        final ZonedDateTime date3 = SyncMonkeyUtils.parseSasUrlDate("2020-10-31");

        assertEquals(Month.OCTOBER, date1.getMonth());
        assertEquals(31, date1.getDayOfMonth());
        assertEquals(2020, date1.getYear());
        assertEquals(12, date1.getHour());
        assertEquals(7, date1.getMinute());
        assertEquals(25, date1.getSecond());

        assertEquals(Month.OCTOBER, date2.getMonth());
        assertEquals(31, date2.getDayOfMonth());
        assertEquals(2020, date2.getYear());
        assertEquals(12, date2.getHour());
        assertEquals(7, date2.getMinute());
        assertEquals(0, date2.getSecond());

        assertEquals(Month.OCTOBER, date3.getMonth());
        assertEquals(31, date3.getDayOfMonth());
        assertEquals(2020, date3.getYear());
        assertEquals(0, date3.getHour());
        assertEquals(0, date3.getMinute());
        assertEquals(0, date3.getSecond());
    }
}