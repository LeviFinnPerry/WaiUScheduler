package com.example.waiuscheduler.parsing;

import android.content.Context;
import android.util.Log;

import com.example.waiuscheduler.database.AppDatabase;
import com.example.waiuscheduler.database.DatabaseController;
import com.example.waiuscheduler.database.DateConverter;
import com.example.waiuscheduler.database.tables.AssessmentEntity;
import com.example.waiuscheduler.database.tables.EventEntity;
import com.example.waiuscheduler.database.tables.PaperEntity;
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

    /// Full pipeline to scrape course outlines and save it into tables
    /// @param url URL address of paper outline
    /// @param callback Callback for pipeline
    public void startCourseOutlinePipeline(HttpUrl url, RepositoryCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                // Get the document with course outline scraper
                Document paperOutline = scraper.getCourseOutline(url);
                if (paperOutline.childNodeSize() > 2) {
                    cleanCurrentOutline(paperOutline);
                    writeToDatabase(); // Write information to database
                    callback.OnComplete("Success"); // Callback for successful pipeline
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

    /// Callback for pipeline
    public interface RepositoryCallback {
        void OnComplete(String result);
    }

    /// Get the database controller
    /// @return Database controller
    public static DatabaseController getDbController() {
        return dbController;
    }

    /// Pre initialising the semester table to this years dates
    private void preInitialiseSemesters() {
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

    /// Individually writes information to database
    private void writeToDatabase() {
        // Write the data to the database
        savePaper();
        saveStaff();
        saveAssessments();
        saveTimetablePattern();
        saveEvents();
    }

    /// Sets the current outline
    private void cleanCurrentOutline(Document paperOutline) {
        this.currentOutline = cleaner.clean(paperOutline);
    }

    /// Add the paper information to database
    private void savePaper() {
        PaperEntity paper = currentOutline.getPaper();
        dbController.savePaper(paper);
    }

    /// Add the staff members to the database
    private void saveStaff() {
        ArrayList<StaffEntity> staffMembers = currentOutline.getStaffs();
        // Set the paperId as foreign key
        for (StaffEntity staff : staffMembers) {
            dbController.saveStaff(staff);      // Save each staff member to database
        }
    }

    /// Add the assessments to the database
    private void saveAssessments() {
        ArrayList<AssessmentEntity> assessments = currentOutline.getAssessments();
        for (AssessmentEntity assessment : assessments) {
            dbController.saveAssessment(assessment);    // Save each assessment to database
        }
    }

    /// Add the timetable pattern to the database
    private void saveTimetablePattern() {
        ArrayList<TimetablePatternEntity> timetablePatterns =
                currentOutline.getTimetablePatterns();
        for (TimetablePatternEntity timetablePattern : timetablePatterns) {
            dbController.saveTimetablePattern(timetablePattern);    // Save timetable to database
        }
    }

    /// Add the events to the database
    private void saveEvents() {
        currentOutline.setEvents(cleaner.createEventData(getCurrentSemester()));
        ArrayList<EventEntity> events = currentOutline.getEvents();
        for (EventEntity event : events) {
            dbController.saveEvent(event); // Save event to database
        }
    }

    /// Returns current semester
    /// @return semester entity
    private SemesterEntity getCurrentSemester() {
        return dbController.getSemester(cleaner.semester_fk);
    }
}
