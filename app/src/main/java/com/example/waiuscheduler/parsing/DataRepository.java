package com.example.waiuscheduler.parsing;

import android.content.Context;
import android.util.Log;

import com.example.waiuscheduler.database.AppDatabase;
import com.example.waiuscheduler.database.DatabaseController;
import com.example.waiuscheduler.database.DateConverter;
import com.example.waiuscheduler.database.tables.AssessmentEntity;
import com.example.waiuscheduler.database.tables.EventEntity;
import com.example.waiuscheduler.database.tables.SemesterEntity;
import com.example.waiuscheduler.database.tables.StaffEntity;
import com.example.waiuscheduler.database.tables.TimetablePatternEntity;

import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Objects;

import okhttp3.HttpUrl;

public class DataRepository {
    private final CourseOutlineScraper scraper;
    private final DataCleaner cleaner;

    private static DatabaseController dbController;
    private ScrapedData currentOutline = new ScrapedData();

    /// Constructor to connect all initialisations
    /// @param context Instance for the app database
    public DataRepository(Context context) {
        this.scraper = new CourseOutlineScraper();
        this.cleaner = new DataCleaner();
        AppDatabase db = AppDatabase.getInstance(context);
        dbController = new DatabaseController(db);
        preInitialiseSemesters();
    }

    /// Pre-initialises semester dates for this year
    private void preInitialiseSemesters()  {
        // Pre initialising the semester table to this years dates
        AppDatabase.databaseWriteExecutor.execute(() -> {
            // A Semester
            dbController.saveSemester(new SemesterEntity("26A",
                    DateConverter.stringToDate("02/03/2026"),
                    DateConverter.stringToDate("26/06/2026"),
                    DateConverter.stringToDate("08/04/2026"),
                    DateConverter.stringToDate("20/04/2026")));
            // B Semester
            dbController.saveSemester(new SemesterEntity("26B",
                    DateConverter.stringToDate("13/07/2026"),
                    DateConverter.stringToDate("06/11/2026"),
                    DateConverter.stringToDate("24/08/2026"),
                    DateConverter.stringToDate("07/09/2026")));
        });
    }

    /// Full pipeline to scrape course outlines and save it into tables
    /// @param url URL address of paper outline
    /// @param callback Callback for pipeline
    public void startCourseOutlinePipeline(HttpUrl url, RepositoryCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                // Get the document with course outline scraper
                Document paperOutline = scraper.getCourseOutline(url);
                if (paperOutline.childNodeSize() > 2) {
                    currentOutline = cleaner.clean(paperOutline); // Clean the data

                    writeToDb(); // Write the data to the database

                    // Callback for successful pipeline
                    callback.OnComplete("Success");
                } else {
                    callback.OnComplete("There is no paper outline for this paper");
                }
            } catch (Exception e) {
                Log.e("Pipeline failure:", Objects.requireNonNull(e.getMessage()));
                if (e.getMessage().contains("code=400")) {
                    callback.OnComplete("Paper Outline does not exist, make sure the information is correct");
                }
            }
        });
    }

    /// Writes all entities to the database
    private void writeToDb() {
        savePaper(); // Add the paper information to database
        saveStaff(); // Add the staff members to the database
        saveAssessment(); // Add the assessments to the database
        saveTimetable(); // Add the timetable pattern to the database
        saveEvents(); // Add the events to the database
    }

    /// Saves paper information from the outline to database
    private void savePaper() {
        dbController.savePaper(currentOutline.getPaper());
    }

    /// Saves staff information from the outline to database
    private void saveStaff() {
        for (StaffEntity staff : currentOutline.getStaffs()) {
            dbController.saveStaff(staff);      // Save each staff member to database
        }
    }

    /// Saves assessment information from the outline to database
    private void saveAssessment() {
        for (AssessmentEntity assessment : currentOutline.getAssessments()) {
            dbController.saveAssessment(assessment);    // Save each assessment to database
        }
    }

    /// Saves timetable information from the outline to database
    private void saveTimetable() {
        for (TimetablePatternEntity timetablePattern : currentOutline.getTimetablePatterns()) {
            dbController.saveTimetablePattern(timetablePattern);    // Save timetable to database
        }
    }

    /// Saves event information from the outline to database
    private void saveEvents() {
        SemesterEntity semester = resolveSemester(currentOutline.getSemesterCode());
        if (semester == null) {
            eventError();
        } else {
            ArrayList<EventEntity> events = cleaner.createEventData(semester);
            currentOutline.setEvents(events);
            for (EventEntity event : events) {
                dbController.saveEvent(event); // Save event to database
            }
        }
    }

    /// Logs error for semester code not found
    private void eventError() {
        Log.e("Data Repository", "Semester not found for code: " + currentOutline.getSemesterCode());
    }

    /// Callback for pipeline
    public interface RepositoryCallback {
        /// @param result String to echo
        void OnComplete(String result);
    }

    /// Get the database controller
    /// @return Database controller
    public static DatabaseController getDbController() {
        return dbController;
    }

    /// Get the semester for the paper outline
    /// @param semesterCode semester to resolve
    /// @return semester matching code
    private SemesterEntity resolveSemester(String semesterCode) {
        return dbController.getSemester(semesterCode);
    }

}
