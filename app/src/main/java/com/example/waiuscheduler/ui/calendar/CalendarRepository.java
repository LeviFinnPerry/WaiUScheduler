package com.example.waiuscheduler.ui.calendar;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.waiuscheduler.database.AppDatabase;
import com.example.waiuscheduler.database.DatabaseController;
import com.example.waiuscheduler.database.tables.AssessmentEntity;
import com.example.waiuscheduler.database.tables.EventEntity;
import com.example.waiuscheduler.database.tables.StudySessionEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CalendarRepository {
    private final DatabaseController dbController;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // Cached sources within the date range
    private LiveData<List<AssessmentEntity>> assessmentSource;
    private LiveData<List<EventEntity>> eventSource;
    private LiveData<List<StudySessionEntity>> studysessionSource;

    private final MediatorLiveData<List<CalendarOccurrence>> calendarItems = new MediatorLiveData<>();

    // Merged list sources

    private List<AssessmentEntity> latestAssessments = Collections.emptyList();
    private List<EventEntity> latestEvents = Collections.emptyList();
    private List<StudySessionEntity> latestStudySessions = Collections.emptyList();


    ///  Constructor for the repository
    /// @param app application instance
    public CalendarRepository(Application app) {
        this.dbController = new DatabaseController(AppDatabase.getInstance(app));
    }

    /// When calendar window changes
    /// @param start start time of the window
    /// @param end end time of the window
    public void calendarRange(long start, long end) {
        // Clear all sources
        if (assessmentSource != null) calendarItems.removeSource(assessmentSource);
        if (eventSource != null) calendarItems.removeSource(eventSource);
        if (studysessionSource != null) calendarItems.removeSource(studysessionSource);

        // Get all sources in the ranges
        assessmentSource = dbController.getAssessmentsBetween(start, end);
        eventSource = dbController.getEventsBetween(start, end);
        studysessionSource = dbController.getStudySessionsBetween(start, end);

        // Add each source to the list
        calendarItems.addSource(assessmentSource, list -> {
            latestAssessments = list;
            convertSources();
        });
        calendarItems.addSource(eventSource, list -> {
            latestEvents = list;
            convertSources();
        });
        calendarItems.addSource(studysessionSource, list -> {
            latestStudySessions = list;
            convertSources();
        });
    }

    /// Converts each type to a calendar occurance
    public void convertSources() {
        List<CalendarOccurrence> all = new ArrayList<>();

        // Convert all assessments
        for (AssessmentEntity a: latestAssessments) {
            all.add(CalendarOccurrence.from(a));
        }

        // Convert all events
        for (EventEntity e: latestEvents) {
            all.add(CalendarOccurrence.from(e));
        }

        // Convert all study sessions
        for (StudySessionEntity s: latestStudySessions) {
            all.add(CalendarOccurrence.from(s));
        }

        // Sort all occurrences
        all.sort(Comparator.comparing(CalendarOccurrence::getStartDateTime));

        // Add to calendar items
        calendarItems.setValue(all);
    }

    /// Returns all occurrences for the calendar
    /// @return All calendar items
    public LiveData<List<CalendarOccurrence>> getOccurrences() { return calendarItems; }





}
