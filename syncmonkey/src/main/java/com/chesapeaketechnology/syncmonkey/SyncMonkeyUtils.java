package com.chesapeaketechnology.syncmonkey;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.core.util.Pair;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * A collection of utilities for use throughout the Sync Monkey app.
 *
 * @since 0.0.8
 */
@SuppressWarnings("WeakerAccess")
public final class SyncMonkeyUtils
{
    private static final String LOG_TAG = SyncMonkeyUtils.class.getSimpleName();

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HHmm zzz").withZone(ZoneId.systemDefault());

    /**
     * Copies the provided input stream to the provided output stream.
     *
     * @throws IOException If the first byte cannot be read for any reason other than the end of the file, if the input stream has been closed, or if some
     *                     other I/O error occurs.
     */
    public static void copyInputStreamToOutputStream(InputStream in, OutputStream out) throws IOException
    {
        final byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1)
        {
            out.write(buffer, 0, read);
        }
    }

    /**
     * Get the name of a file without the file extension or period.
     *
     * @param fileName File name to work on
     * @return file name without extension
     */
    public static String getNameWithoutExtension(String fileName)
    {
        int i = fileName.lastIndexOf('.');

        if (i > 0 && i < fileName.length() - 1)
        {
            return fileName.substring(0, i);
        }
        return fileName;
    }

    /**
     * Extract the extension (with the period) from the given file name.
     *
     * @param fileName File name to process
     * @return file extension with the period, or null if no period or nothing after the period
     */
    public static String getExtension(String fileName)
    {
        String ext = null;
        int i = fileName.lastIndexOf('.');

        if (i > 0 && i < fileName.length() - 1)
        {
            ext = fileName.substring(i).toLowerCase();
        }
        return ext;
    }

    /**
     * @return The File object representing the app's private storage directory that is synced with the remote server.
     * Any files in this directory will be uploaded to the remote server.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File getPrivateAppFilesSyncDirectory(Context context)
    {
        final File privateAppFilesSyncDirectory = new File(context.getFilesDir(), SyncMonkeyConstants.PRIVATE_SHARED_SYNC_DIRECTORY);
        if (!privateAppFilesSyncDirectory.exists()) privateAppFilesSyncDirectory.mkdir();
        return privateAppFilesSyncDirectory;
    }

    /**
     * Gets a human readable date/time string to display to the user.
     *
     * @param localDateTime The date/time to convert to a string.
     * @return The human readable date/time string.
     * @since 1.0.0
     */
    public static String getDateTimeString(LocalDateTime localDateTime)
    {
        return DATE_TIME_FORMATTER.format(localDateTime);
    }

    /**
     * Parses an ISO 8601 compliant date string into a Java Date. Only a subset of ISO 8601
     * dates are supported. See Azure documentation for more details on valid date strings:
     * https://docs.microsoft.com/en-us/rest/api/storageservices/create-service-sas#specifying-the-signature-validity-interval
     * <p>
     * See also:
     * https://en.wikipedia.org/wiki/ISO_8601
     *
     * @param dateString Date to parse
     * @return Java Date. Will return null if passed a date of unrecognized format.
     * @since 0.1.2
     */
    static ZonedDateTime parseSasUrlDate(String dateString)
    {
        if (dateString == null) return null;

        if (dateString.length() == 10)
        {
            LocalDate ld = LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE);
            return ld.atStartOfDay().atZone(ZoneId.of("UTC"));
        }

        boolean timeWithoutSeconds = Pattern.compile("T[0-9]{2}:[0-9]{2}([+\\-Z])").matcher(dateString).find();
        boolean timeWithSeconds = Pattern.compile("T([0-9]{2}:){2}[0-9]{2}([+\\-Z])").matcher(dateString).find();

        if (timeWithSeconds || timeWithoutSeconds)
        {
            return ZonedDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME);
        }

        Log.wtf(LOG_TAG, "Cannot parse date in unrecognized format");
        return null;
    }

    /**
     * Get the message to display to the user about when their SAS URL expires.
     * Will return a message indicating invalidity if the current date is not within
     * the URL's date range.
     *
     * @param now     Current date
     * @param starts  Start date of URL validity
     * @param expires End date of URL validity
     * @return A pair whose first element indicates if the date is valid,
     * and whose second is the expiration message to display in the UI.
     * @since 0.1.2
     */
    static Pair<Boolean, String> getUrlExpirationMessage(ZonedDateTime now, ZonedDateTime starts, ZonedDateTime expires)
    {
        if (now.isBefore(starts))
        {
            String message = "SAS URL not valid until ";
            final long daysBetween = DAYS.between(now, starts);

            if (daysBetween == 0)
            {
                // SAS URL becomes valid sometime today, but not yet. Rather than following the infinite rabbit-hole
                // of displaying "Not valid for x hours/minutes," it's simpler to just show this message.
                message = "SAS URL not yet within valid date range";
            } else if (daysBetween == 1)
            {
                message += "1 day from now";
            } else
            {
                message += daysBetween + " days from now";
            }

            return new Pair<>(false, message);
        } else if (now.isAfter(starts) && now.isBefore(expires))
        {
            String message = "SAS URL expires ";
            final long daysBetween = DAYS.between(now, expires);

            if (daysBetween == 0)
            {
                message += "today";
            } else if (daysBetween == 1)
            {
                message += "tomorrow";
            } else
            {
                message += "in " + daysBetween + " days";
            }

            return new Pair<>(true, message);
        }

        return new Pair<>(false, "SAS URL is expired");
    }
}
