package com.example.waiuscheduler.parsing;

import android.content.Context;
import android.util.Log;

import com.example.waiuscheduler.database.AppDatabase;
import com.example.waiuscheduler.database.DatabaseController;
import com.example.waiuscheduler.database.DateConverter;
import com.example.waiuscheduler.database.tables.AssessmentTable;
import com.example.waiuscheduler.database.tables.EventTable;
import com.example.waiuscheduler.database.tables.PaperTable;
import com.example.waiuscheduler.database.tables.SemesterTable;
import com.example.waiuscheduler.database.tables.StaffTable;
import com.example.waiuscheduler.database.tables.TimetablePatternTable;

import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Objects;

import okhttp3.HttpUrl;

public class DataRepository {
    private final CourseOutlineScraper scraper;
    private final DataCleaner cleaner;

    private final DatabaseController dbController;
    private ScrapedData currentOutline = new ScrapedData();

    /// Constructor to connect all initialisations
    public DataRepository(Context context) {
        this.scraper = new CourseOutlineScraper();
        this.cleaner = new DataCleaner();
        AppDatabase db = AppDatabase.getInstance(context);
        this.dbController = new DatabaseController(db);


        // Pre initialising the semester table to this years dates
        AppDatabase.databaseWriteExecutor.execute(() -> {
            // A Semester
            dbController.saveSemester(new SemesterTable("26A",
                    DateConverter.stringToDate("02/03/2026"),
                    DateConverter.stringToDate("26/06/2026"),
                    DateConverter.stringToDate("08/04/2026"),
                    DateConverter.stringToDate("20/04/2026")));
            // B Semester
            dbController.saveSemester(new SemesterTable("26B",
                    DateConverter.stringToDate("13/07/2026"),
                    DateConverter.stringToDate("06/11/2026"),
                    DateConverter.stringToDate("24/08/2026"),
                    DateConverter.stringToDate("07/09/2026")));

        });
    }

    /// Full pipeline to scrape course outlines and save it into tables
    public void startCourseOutlinePipeline(HttpUrl url, RepositoryCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                // Get the document with course outline scraper
                Document paperOutline = scraper.getCourseOutline(url);

                if (paperOutline.childNodeSize() > 2) {
                    // Clean the data
                    currentOutline = cleaner.clean(paperOutline);

                    // Write the data to the database

                    // Add the paper information to database
                    PaperTable paper = currentOutline.getPaper();
                    dbController.savePaper(paper);

                    // Add the staff members to the database
                    ArrayList<StaffTable> staffMembers = currentOutline.getStaffs();
                    // Set the paperId as foreign key
                    for (StaffTable staff : staffMembers) {
                        dbController.saveStaff(staff);      // Save each staff member to database
                    }

                    // Add the assessments to the database
                    ArrayList<AssessmentTable> assessments = currentOutline.getAssessments();
                    for (AssessmentTable assessment : assessments) {
                        dbController.saveAssessment(assessment);    // Save each assessment to database
                    }

                    // Add the events to the database
                    ArrayList<TimetablePatternTable> timetablePatterns =
                            currentOutline.getTimetablePatterns();
                    for (TimetablePatternTable timetablePattern : timetablePatterns) {
                        dbController.saveTimetablePattern(timetablePattern);    // Save timetable to database
                    }

                    // Get the current semester for the paper
                    SemesterTable semesterTable =
                            dbController.getSemesterTable(cleaner.semester_fk);

                    currentOutline.setEvents(cleaner.createEventData(semesterTable));
                    ArrayList<EventTable> events = currentOutline.getEvents();
                    for (EventTable event : events) {
                        dbController.saveEvent(event); // Save event to database
                    }
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

    /// Callback for pipeline
    public interface RepositoryCallback {
        void OnComplete(String result);
    }

    /// Get the database controller
    public DatabaseController getDbController() {
        return dbController;
    }

}
