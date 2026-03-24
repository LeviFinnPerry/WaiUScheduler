package com.example.waiuscheduler.database;

import android.util.Log;

import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateConverter {
    private static final String DATE_PATTERN = "dd/MM/yyyy";
    private static final String TIME_PATTERN = "HH:mm";
    private static final String DATETIME_PATTERN = "dd/MM/yyyy HH:mm";
    private static final Locale LOCALE = new Locale("en", "NZ");

    /// Converts long value stored in db timestamp
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    /// Converts date objects to timestamps
    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    /// Converts string to date (date object)
    public static Date stringToDate(String dateString) {
        return parse(dateString, DATE_PATTERN);
    }

    /// Converts string to time (date object)
    public static Date stringToTime(String timeString) {
        return parse(timeString, TIME_PATTERN);
    }

    /// Combines a date and a time into a datetime
    public static Date ToDateTime(Date date, Date time) {
        String dateString = format(date, DATE_PATTERN);
        String timeString = format(time, TIME_PATTERN);
        String dateTimeString = dateString + " " + timeString;
        return parse(dateTimeString, DATETIME_PATTERN);
    }

    /// Primary function to parse a string into a simple date format based on the pattern
    private static Date parse(String str, String pattern) {
        try {
            return new SimpleDateFormat(pattern, LOCALE).parse(str); // Returns new date format
        } catch (ParseException e) {
            Log.e("DateConverter", "Parsing error for date");   // Returns error
            return null;
        }
    }

    /// Primary function to format a date object back into a string based on the format
    private static String format(Date date, String pattern) {
        if (date == null) return "";    // If there is no date return an empty string
        try {
            return new SimpleDateFormat(pattern, LOCALE).format(date);  // Returns new string
        } catch (Exception e) {
            Log.e("DateConverter", "Formatting error for date");    // Returns error
            return "";
        }
    }

}
