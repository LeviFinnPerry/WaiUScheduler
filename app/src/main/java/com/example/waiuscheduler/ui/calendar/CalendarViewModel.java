package com.example.waiuscheduler.ui.calendar;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class CalendarViewModel extends AndroidViewModel {

    // Private variables
    private final MutableLiveData<Calendar> currentDate = new MutableLiveData<>();
    private final MutableLiveData<String> viewMode = new MutableLiveData<>();
    private final MutableLiveData<List<String>> filters = new MutableLiveData<>();
    private final MutableLiveData<List<CalendarOccurrence>> events = new MutableLiveData<>();


    /// Constructor for the view model
    /// @param application Application for the view
    public CalendarViewModel(@NonNull Application application) {
        super(application);
        currentDate.setValue(Calendar.getInstance());
        viewMode.setValue("month");
        filters.setValue(new ArrayList<>(Arrays.asList(
                "Study", "Lecture", "Assessment")));
    }

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
    public MutableLiveData<List<String>> getFilters() {
        return filters;
    }

    /// Gets all the events for the calendar
    /// @return events
    public MutableLiveData<List<CalendarOccurrence>> getEvents() {
        return events;
    }
}