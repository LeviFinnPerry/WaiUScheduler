package com.example.waiuscheduler.database;

import android.util.Log;

import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/// Type Converter to handle Date Objects in the app
public class DateConverter {
    private static final String DATE_PATTERN = "dd/MM/yyyy";
    private static final String ABV_DATE_PATTERN = "dd MMM yyyy";
    private static final String TIME_PATTERN = "HH:mm";
    private static final String DATETIME_PATTERN = "dd/MM/yyyy HH:mm";
    private static final Locale LOCALE = new Locale("en", "NZ");

    /// Converts long value stored in the database timestamp
    /// @param value Long timestamp
    /// @return Date from timestamp
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    /// Converts date objects to timestamps
    /// @param date Date object
    /// @return Long timestamp
    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    /// Converts string to date
    /// @param dateString Text formatted date
    /// @return Date object
    public static Date stringToDate(String dateString) {
        return parse(dateString, DATE_PATTERN);
    }

    /// Converts abbreviated string to date
    /// @param dateString Text formatted date with shortened month
    /// @return Date object
    public static Date stringAbvToDate(String dateString) {
        return parse(dateString, ABV_DATE_PATTERN);
    }

    /// Converts string to time
    /// @param timeString Text formatted time
    /// @return Date object for the time
    public static Date stringToTime(String timeString) {
        return parse(timeString, TIME_PATTERN);
    }

    /// Combines a date and a time into a datetime
    /// @param date Date object
    /// @param time Date object for the time
    /// @return Full date and time
    public static Date toDateTime(Date date, Date time) {
        String dateString = format(date, DATE_PATTERN);
        String timeString = format(time, TIME_PATTERN);
        String dateTimeString = dateString + " " + timeString;
        return parse(dateTimeString, DATETIME_PATTERN);
    }

    /// Primary function to parse a string into a simple date format based on the pattern
    /// @param str Text format of a date object
    /// @param pattern Date format of the text
    /// @return Date object
    private static Date parse(String str, String pattern) {
        try {
            // Returns new date format
            return new SimpleDateFormat(pattern, LOCALE).parse(str);
        } catch (ParseException e) {
            Log.e("DateConverter", "Parsing error for date");   // Returns error
            return null;
        }
    }

    /// Primary function to format a date object back into a string based on the format
    /// @param date Date object
    /// @param pattern Date format for the text
    /// @return Text format of the date object
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
