package com.cybor.studhelper.utils;

import android.content.Context;

import com.cybor.studhelper.R;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.PeriodFormatterBuilder;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.TimeZone;

public class Utils
{
    public static String getFormattedDuration(Duration duration)
    {
        return getFormattedDuration(duration, true);
    }

    public static String getFormattedDuration(Duration duration, boolean withSeconds)
    {
        PeriodFormatterBuilder builder = new PeriodFormatterBuilder()
                .printZeroAlways()
                .minimumPrintedDigits(2)
                .appendHours()
                .appendSeparator(":")
                .appendMinutes();
        if (withSeconds)
            builder = builder.appendSeparator(":").appendSeconds();
        return builder.toFormatter().print(duration.toPeriod());
    }

    public static DateTime parseTime(String source)
    {
        String[] data = source.split(":");
        if (data.length == 2)
            return new DateTime(1970, 1, 1, Integer.parseInt(data[0]), Integer.parseInt(data[1]));
        return null;
    }

    public static Duration parseDuration(String source)
    {
        String[] data = source.split(":");
        if (data.length == 2)
            return new Duration((Integer.parseInt(data[0]) * 60 + Integer.parseInt(data[1])) * 60 * 1000);
        return null;
    }

    public static DateTime getCurrentTime()
    {
        return new DateTime(Calendar.getInstance().getTime(), DateTimeZone.forOffsetHours(TimeZone.getDefault().getRawOffset() / 1000 / 60 / 60))
                .withDate(1970, 1, 1);
    }

    public static String getFormattedTime(DateTime time)
    {
        return new DateTimeFormatterBuilder().appendHourOfDay(2).appendLiteral(':').appendMinuteOfHour(2).toFormatter().print(time);
    }

    public static boolean checkServerConnection(Context context)
    {
        byte[] data = downloadData("http://feopoliteh.ru/");
        return data != null && new String(data).contains(context.getString(R.string.testServerResponse));
    }

    public static boolean downloadFile(String url, String localUrl) throws IOException
    {
        byte[] data = downloadData(url);
        if (data != null)
        {
            new FileOutputStream(localUrl).write(data);
            return true;
        } else return false;
    }

    public static byte[] downloadData(String url)
    {
        try
        {
            String[] blocks = url.split("/");
            String baseUrl = url.split(blocks[blocks.length - 1])[0];
            String filename = blocks[blocks.length - 1];

            URL toDownload = new URL(baseUrl + URLEncoder.encode(filename, "UTF-8"));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            byte[] chunk = new byte[4096];
            int bytesRead;
            InputStream stream = toDownload.openStream();

            while ((bytesRead = stream.read(chunk)) > 0)
                outputStream.write(chunk, 0, bytesRead);
            return outputStream.toByteArray();
        } catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }

    }
}
