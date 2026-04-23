package com.example.waiuscheduler.ui.calendar;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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

        reloadRange();

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

        reloadRange();

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
        reloadRange();
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
    /// Gets the range of dates currently visible in the view
    /// @return start and end time stamp
    public long[] getVisibleRange() {
        Calendar c = (Calendar) Objects.requireNonNull(currentDate.getValue()).clone();
        String mode = viewMode.getValue();
        Calendar start = (Calendar) c.clone();
        Calendar end = (Calendar) c.clone();

        if (MODE_MONTH.equals(mode)) {
            start.set(Calendar.DAY_OF_MONTH, 1);
            end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH));
        } else if (MODE_WEEK.equals(mode)) {

            start.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            end.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        }

        // Set day
        setStartDay(start);
        setEndDay(end);

        return new long[] {start.getTimeInMillis(), end.getTimeInMillis()};
    }

    /// Helping function to set to beginning of day
    /// @param start calendar day
    private void setStartDay(Calendar start) {
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

    }

    /// Helping function to set to end of the day
    /// @param end calendar day
    private void setEndDay(Calendar end) {
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 999);
    }

    /// Reloads the visible range of the calendar view
    private void reloadRange() {
        long[] range = getVisibleRange();
        calendarRepository.calendarRange(range[0], range[1]);
    }

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

    /// Get all occurrences from the repository
    /// @return List of all calendar items in view
    public LiveData<List<CalendarOccurrence>> getOccurrences() {
        return calendarRepository.getOccurrences();
    }
}