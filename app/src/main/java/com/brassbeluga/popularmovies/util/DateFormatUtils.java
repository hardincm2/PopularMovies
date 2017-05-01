package com.brassbeluga.popularmovies.util;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Class containing convenience methods for formatting and reading dates
 */
public class DateFormatUtils {

    /**
     * Reads a date according the format specification and returns the year.
     *
     * @param dateFormat A standardized date format
     * @param date The date String itself
     * @return The year of the date.
     */
    public static int getYear(String dateFormat, String date) {
        DateFormat df = new SimpleDateFormat(dateFormat);
        Date releaseDate = null;
        try {
            releaseDate = df.parse(date);
        } catch (ParseException e) {
            Log.e("", "Unable to read date information");
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(releaseDate);
        return cal.get(Calendar.YEAR);
    }
}
