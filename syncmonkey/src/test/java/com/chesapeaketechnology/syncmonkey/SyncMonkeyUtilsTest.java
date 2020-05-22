package com.chesapeaketechnology.syncmonkey;

import androidx.core.util.Pair;
import org.junit.Test;
import java.util.Date;
import static org.junit.Assert.*;

/**
 * Tests for main activity
 */
public class SyncMonkeyUtilsTest
{
    @Test
    public void urlExpirationMessageIsCorrect()
    {
        final int oneDay = 1000 * 60 * 60 * 24;
        final Date now = new Date();
        final Date tenDaysAgo = new Date(now.getTime() - (10 * oneDay));
        final Date yesterday = new Date(now.getTime() - oneDay);
        final Date earlierToday = new Date(now.getTime() - 1); // see [1]
        final Date laterToday = new Date(now.getTime() + 1); // see [1]
        final Date tomorrow = new Date(now.getTime() + oneDay);
        final Date tenDaysFromNow = new Date(now.getTime() + (10 * oneDay));

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

        // [1] These will technically fail if you happen to run them at exactly one millisecond
        // before or after midnight. If that is the case, close your IDE and go to sleep.
    }
}