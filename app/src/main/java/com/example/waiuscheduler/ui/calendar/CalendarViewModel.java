package com.example.waiuscheduler.ui.calendar;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CalendarViewModel extends AndroidViewModel {

    // Constant variables
    public static final String MODE_DAY = "day";
    public static final String MODE_WEEK = "week";
    public static final String MODE_MONTH = "month";

    // Private variables
    private final MutableLiveData<Calendar> currentDate = new MutableLiveData<>();
    private final MutableLiveData<String> viewMode = new MutableLiveData<>();
    private final MutableLiveData<Set<String>> filters = new MutableLiveData<>();
    private final CalendarRepository calendarRepository;


    /// Constructor for the view model
    /// @param application Application for the view
    public CalendarViewModel(@NonNull Application application) {
        super(application);
        calendarRepository = new CalendarRepository(application);
        currentDate.setValue(Calendar.getInstance());
        viewMode.setValue(MODE_MONTH);
        filters.setValue(new HashSet<>(Arrays.asList(
                CalendarOccurrence.TYPE_ASSESSMENT,
                CalendarOccurrence.TYPE_EVENT,
                CalendarOccurrence.TYPE_STUDY)));

        // Reload the range of view

    }

    // Navigation

    private void shiftDate(int delta) {
        Calendar c = currentDate.getValue();
        if (c == null) return;  // Return if no date
        Calendar copy = (Calendar) c.clone();
        String mode = viewMode.getValue();
        // Check if day, week or month
        if (MODE_DAY.equals(mode)) { copy.add(Calendar.DAY_OF_MONTH, delta); }
        else if (MODE_WEEK.equals(mode)) { copy.add(Calendar.WEEK_OF_YEAR, delta); }
        else { copy.add(Calendar.MONTH, delta); }
        currentDate.setValue(copy);

        // Reload the range of view

    }

    /// Moves to previous occurrences of view
    public void goToPrevious() { shiftDate(-1); }
    /// Moves to future occurrences of view
    public void goToNext() { shiftDate(+1); }
    /// Moves back to today's occurrences of view
    public void goToToday() { currentDate.setValue(Calendar.getInstance());}

    // View mode

    /// Set the view mode
    public void setViewMode(String mode) {
        viewMode.setValue(mode);

        // Reload the range of view
    }

    // Filters
    /// Helping function to determine if the filter is active
    /// @param type type of the filter
    /// @return boolean if the filter is active
    public boolean isFilterActive(String type) {
        Set<String> f = filters.getValue();
        return f != null && f.contains(type);
    }

    /// Toggles filter for different types of occurrences
    /// @param type Type of calendar occurrence
    public void toggleFilter(String type) {
        // Sets current to filters or if none selected
        Set<String> current = new HashSet<>(
                filters.getValue() != null ? filters.getValue() : Collections.emptySet());

        // Toggles on and off
        if (current.contains(type)) current.remove(type);
        else current.add(type);

        filters.setValue(current);
    }

    // Range calculation
    // TODO: Calculate range of view

    // Getters
    /// Gets the current day
    /// @return current date
    public MutableLiveData<Calendar> getCurrentDate() {
        return currentDate;
    }

    /// Gets the calendar size
    /// @return view mode
    public MutableLiveData<String> getViewMode() {
        return viewMode;
    }

    /// Get any filters on the calendar
    /// @return filters
    public MutableLiveData<Set<String>> getFilters() {
        return filters;
    }

    /// Get calendar repository instance
    /// @return calendar repository
    public CalendarRepository getCalendarRepository() {
        return calendarRepository;
    }
}