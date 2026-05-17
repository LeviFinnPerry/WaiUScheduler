package com.example.waiuscheduler.ui.calendar.extension;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.waiuscheduler.database.AppDatabase;
import com.example.waiuscheduler.database.DatabaseController;
import com.example.waiuscheduler.database.tables.AssessmentEntity;
import com.example.waiuscheduler.database.tables.EventEntity;
import com.example.waiuscheduler.database.tables.StudySessionEntity;
import com.example.waiuscheduler.parsing.DataRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CalendarRepository {
    private final DatabaseController dbController;

    // Cached sources within the date range
    private LiveData<List<AssessmentEntity>> assessmentSource;
    private LiveData<List<EventEntity>> eventSource;
    private LiveData<List<StudySessionEntity>> studySessionSource;

    private final MediatorLiveData<List<CalendarOccurrence>> calendarItems = new MediatorLiveData<>();

    // Thread handling
    private final Handler mergeHandler = new Handler(Looper.getMainLooper());
    private Runnable mergeRunnable;

    // Merged list sources

    private List<AssessmentEntity> latestAssessments = Collections.emptyList();
    private List<EventEntity> latestEvents = Collections.emptyList();
    private List<StudySessionEntity> latestStudySessions = Collections.emptyList();

    // Handling calendar items
    private List<CalendarOccurrence> all;


    ///  Constructor for the repository
    /// If the constructor isn't initialised yet then it will initialise one
    /// To ensure data consistency and hopefully solve this bug I have been meaning
    /// to fix for weeks but couldn't figure it out with debugging
    public CalendarRepository() {
        this.dbController  = DataRepository.getDbController();
    }

    /// When calendar window changes
    /// @param start start time of the window
    /// @param end end time of the window
    public void calendarRange(long start, long end) {
        Log.d("CAL_DEBUG", "calendarRange called: " + start + " - " + end);

        // Clear all sources
        if (assessmentSource != null) calendarItems.removeSource(assessmentSource);
        if (eventSource != null) calendarItems.removeSource(eventSource);
        if (studySessionSource != null) calendarItems.removeSource(studySessionSource);

        // Cancel pending merges
        if (mergeRunnable != null) mergeHandler.removeCallbacks(mergeRunnable);

        // Get all sources in the ranges
        assessmentSource = dbController.getAssessmentsBetween(start, end);
        eventSource = dbController.getEventsBetween(start, end);
        studySessionSource = dbController.getStudySessionsBetween(start, end);

        // Add each source to the list
        addAssessmentSource();
        addEventSource();
        addSessionSource();
    }

    /// Adds assessments to calendar items
    private void addAssessmentSource() {
        calendarItems.addSource(assessmentSource, list -> {
            latestAssessments = list != null ? list : Collections.emptyList();
            convertSources();
        });
    }

    /// Adds events to calendar items
    private void addEventSource() {
        calendarItems.addSource(eventSource, list -> {
            latestEvents = list != null ? list : Collections.emptyList();
            convertSources();
        });
    }

    /// Adds study sessions to calendar items
    private void addSessionSource() {
        calendarItems.addSource(
                studySessionSource, list -> {
                    latestStudySessions = list != null ? list : Collections.emptyList();
                    convertSources();
                });
    }

    /// Converts each type to a calendar occurance
    public void convertSources() {
        if (mergeRunnable != null) mergeHandler.removeCallbacks(mergeRunnable);
        mergeRunnable = () -> {
            this.all = new ArrayList<>();
            convertAssessments();
            convertEvents();
            convertSessions();
            // Sort all occurrences
            all.sort(Comparator.comparing(CalendarOccurrence::getStartDateTime));
            // Add to calendar items
            calendarItems.setValue(all);
        };
        mergeHandler.postDelayed(mergeRunnable, 50);
    }

    /// Converts assessments to calendar occurrences
    private void convertAssessments() {
        // Convert all assessments
        for (AssessmentEntity a : latestAssessments) {
            all.add(CalendarOccurrence.from(a));
        }
    }

    /// Converts events to calendar occurrences
    private void convertEvents() {
        // Convert all events
        for (EventEntity e : latestEvents) {
            all.add(CalendarOccurrence.from(e));
        }
    }

    /// Converts study to calendar occurrences
    private void convertSessions() {
        // Convert all study sessions
        for (StudySessionEntity s : latestStudySessions) {
            all.add(CalendarOccurrence.from(s));
        }
    }

    /// Returns all occurrences for the calendar
    /// @return All calendar items
    public LiveData<List<CalendarOccurrence>> getOccurrences() { return calendarItems; }


    /// Deletes study session
    /// @param s Study session
    public void deleteStudySession(StudySessionEntity s) {
        AppDatabase.databaseWriteExecutor.execute(() -> dbController.deleteStudySession(s));
    }

    /// Update study session
    /// @param s Study session
    public void updateStudySession(StudySessionEntity s) {
        AppDatabase.databaseWriteExecutor.execute(() -> dbController.updateStudySession(s));
    }

    /// Updates assessment grade
    /// @param a Assessment
    public void updateAssessmentGrade(AssessmentEntity a) {
        AppDatabase.databaseWriteExecutor.execute(() -> dbController.updateAssessment(a));
    }
}
