package com.example.waiuscheduler.database;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

/// Tests for converting dates
/// Verifies string parsing, timestamp conversion, and DateTime merging logic
/// @Link DateConverter
public class DateConverterTest {

    /// Confirms that a standard NZ format date string (dd/MM/yyyy)
    /// is correctly converted to a Date object
    /// @Link stringToDate
    @Test
    public void stringToDate_valid() {
        // Given
        String input = "08/04/2026";

        // When
        Date result = DateConverter.stringToDate(input);

        // Then
        Calendar cal = Calendar.getInstance();
        cal.setTime(result);

        // Assert
        assertAll("Date components",
                () -> assertEquals(2026, cal.get(Calendar.YEAR)),
                () -> assertEquals(Calendar.APRIL, cal.get(Calendar.MONTH)),
                () -> assertEquals(8, cal.get(Calendar.DAY_OF_MONTH))
        );
    }

    /// Strings with incorrect format return null
    /// @Link stringToDate
    @Test
    public void stringToDate_invalid() {
        // Given
        String input = "08-04-2026";

        // When
        Date result = DateConverter.stringToDate(input);

        // Assert
        assertNull(result);
    }

    /// Confirms that a abbreviated month NZ format date string (dd MMM yyyy)
    /// is correctly converted to a Date object
    /// @Link stringAbvToDate
    @Test
    public void stringAbvToDate_valid() {
        // Given
        String input = "08 apr 2026";

        // When
        Date result = DateConverter.stringAbvToDate(input);

        // Then
        Calendar cal = Calendar.getInstance();
        cal.setTime(result);

        // Assert
        assertAll("Date components",
                () -> assertEquals(2026, cal.get(Calendar.YEAR)),
                () -> assertEquals(Calendar.APRIL, cal.get(Calendar.MONTH)),
                () -> assertEquals(8, cal.get(Calendar.DAY_OF_MONTH))
        );
    }

    /// Strings with incorrect format return null
    /// @Link stringAbvToDate
    @Test
    public void stringAbvToDate_invalid() {
        // Given
        String input = "08/apr/2026";

        // When
        Date result = DateConverter.stringAbvToDate(input);

        // Assert
        assertNull(result);
    }

    /// Confirms that a format time string (HH:mm)
    /// is correctly converted to a Date object
    /// @Link stringToTime
    @Test
    public void stringToTime_valid() {
        // Given
        String input = "14:00";

        // When
        Date result = DateConverter.stringToTime(input);

        // Then
        Calendar cal = Calendar.getInstance();
        cal.setTime(result);

        // Assert
        assertAll(
                () -> assertEquals(14, cal.get(Calendar.HOUR_OF_DAY)),
                () -> assertEquals(0, cal.get(Calendar.MINUTE))
        );
    }

    /// Strings with incorrect format return null
    /// @Link stringToTime
    @Test
    public void stringToTime_invalid() {
        // Given
        String input = "1400";

        // When
        Date result = DateConverter.stringToTime(input);

        // Assert
        assertNull(result);
    }


    /// Confirms that a NZ date format date string (dd/MM/yyyy)
    /// and format time string (HH:mm)
    /// is correctly converted to a Date object
    /// @Link toDateTime
    @Test
    public void toDateTime_valid() {
        // Given
        long date_long_input = 1775563200000L;
        long time_long_input = -2208938400000L;

        Date dateInput = new Date(date_long_input);
        Date timeInput = new Date(time_long_input);

        // When
        Date result = DateConverter.toDateTime(dateInput, timeInput);

        // Then
        Calendar cal = Calendar.getInstance();
        cal.setTime(result);

        // Assert
        assertAll(
                () -> assertEquals(2026, cal.get(Calendar.YEAR)),
                () -> assertEquals(Calendar.APRIL, cal.get(Calendar.MONTH)),
                () -> assertEquals(8, cal.get(Calendar.DAY_OF_MONTH)),
                () -> assertEquals(1, cal.get(Calendar.HOUR_OF_DAY)),
                () -> assertEquals(30, cal.get(Calendar.MINUTE))
        );

    }

    /// Returns the default date time (01/01/1970 12:00)
    /// @Link toDateTime
    @Test
    public void toDateTime_invalid() {
        // Given
        Date dateInput = new Date(14);
        Date timeInput = new Date(12);

        // When
        Date result = DateConverter.toDateTime(dateInput, timeInput);

        // Then
        Date errorDate = new Date(0L);  // Default error date

        // Assert
        assertEquals(result, errorDate);

    }
}