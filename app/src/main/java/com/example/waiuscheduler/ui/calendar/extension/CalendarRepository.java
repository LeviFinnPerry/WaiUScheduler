package com.example.waiuscheduler.ui.calendar.extension;

import android.app.Application;
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


    ///  Constructor for the repository
    /// If the constructor isn't initialised yet then it will initialise one
    /// To ensure data consistency and hopefully solve this bug I have been meaning
    /// to fix for weeks but couldn't figure it out with debugging
    /// @param application Application context
    public CalendarRepository(Application application) {
        this.dbController  = new DatabaseController(AppDatabase.getInstance(application));
    }

    /// When calendar window changes
    /// @param start start time of the window
    /// @param end end time of the window
    public void calendarRange(long start, long end) {
        // Clear all sources
        if (assessmentSource != null) calendarItems.removeSource(assessmentSource);
        if (eventSource != null) calendarItems.removeSource(eventSource);
        if (studySessionSource != null) calendarItems.removeSource(studySessionSource);

        // Get all sources in the ranges
        assessmentSource = dbController.getAssessmentsBetween(start, end);
        eventSource = dbController.getEventsBetween(start, end);
        studySessionSource = dbController.getStudySessionsBetween(start, end);

        // Add each source to the list
        calendarItems.addSource(assessmentSource, list -> {
            Log.d("CalRepo", "Assessments emitted: " + (list != null ? list.size() : "null"));
            latestAssessments = list != null ? list : Collections.emptyList();
            convertSources();
        });
        calendarItems.addSource(eventSource, list -> {
            Log.d("CalRepo", "Events emitted: " + (list != null ? list.size() : "null"));
            latestEvents = list != null ? list : Collections.emptyList();
            convertSources();
        });
        calendarItems.addSource(
                studySessionSource, list -> {
            Log.d("CalRepo", "Study emitted: " + (list != null ? list.size() : "null"));
            latestStudySessions = list != null ? list : Collections.emptyList();
            convertSources();
        });
    }

    /// Converts each type to a calendar occurance
    public void convertSources() {
        if (mergeRunnable != null) mergeHandler.removeCallbacks(mergeRunnable);
        mergeRunnable = () -> {
            List<CalendarOccurrence> all = new ArrayList<>();
            // Convert all assessments
            for (AssessmentEntity a : latestAssessments) {
                all.add(CalendarOccurrence.from(a));
            }
            // Convert all events
            for (EventEntity e : latestEvents) {
                all.add(CalendarOccurrence.from(e));
            }
            // Convert all study sessions
            for (StudySessionEntity s : latestStudySessions) {
                all.add(CalendarOccurrence.from(s));
            }
            // Sort all occurrences
            all.sort(Comparator.comparing(CalendarOccurrence::getStartDateTime));
            // Add to calendar items
            calendarItems.setValue(all);
        };
        mergeHandler.postDelayed(mergeRunnable, 50);
    }

    /// Returns all occurrences for the calendar
    /// @return All calendar items
    public LiveData<List<CalendarOccurrence>> getOccurrences() { return calendarItems; }


    /// Deletes study session
    /// @param s Study session
    public void deleteStudySession(StudySessionEntity s) {
        if (mergeRunnable != null) mergeHandler.removeCallbacks(mergeRunnable);
        mergeRunnable = () -> dbController.deleteStudySession(s);
    }

    /// Update study session
    /// @param s Study session
    public void updateStudySession(StudySessionEntity s) {
        if (mergeRunnable != null) mergeHandler.removeCallbacks(mergeRunnable);
        mergeRunnable = () -> dbController.updateStudySession(s);
    }
}
