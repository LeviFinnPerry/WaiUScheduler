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
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    public static Date stringToDate(String dateString) {
        return parse(dateString, DATE_PATTERN);
    }

    public static Date stringToTime(String timeString) {
        return parse(timeString, TIME_PATTERN);
    }

    public static Date stringToDateTime(String dateString, String timeString) {
        String dateTimeString = dateString + " " + timeString;
        return parse(dateTimeString, DATETIME_PATTERN);
    }

    private static Date parse(String str, String pattern) {
        try {
            return new SimpleDateFormat(pattern, LOCALE).parse(str);
        } catch (ParseException e) {
            Log.e("DateConverter", "Parsing error for date");
            return null;
        }
    }
}
