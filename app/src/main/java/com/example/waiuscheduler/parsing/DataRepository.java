package com.example.waiuscheduler.parsing;

import android.content.Context;

import androidx.room.Transaction;

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

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.HttpUrl;

public class DataRepository {
    private final CourseOutlineScraper scraper;
    private final DataCleaner cleaner;
    private final AppDatabase db;

    private final DatabaseController dbController;
    private ScrapedData currentOutline = new ScrapedData();


    public DataRepository(Context context) {
        this.scraper = new CourseOutlineScraper();
        this.cleaner = new DataCleaner();
        this.db = AppDatabase.getInstance(context);
        this.dbController = new DatabaseController(db);



        AppDatabase.databaseWriteExecutor.execute(() -> {
            // Add the dates
            dbController.saveSemester(new SemesterTable("26A",
                    DateConverter.stringToDate("02/03/2026"),
                    DateConverter.stringToDate("26/06/2026"),
                    DateConverter.stringToDate("08/04/2026"),
                    DateConverter.stringToDate("20/04/2026")));
            dbController.saveSemester(new SemesterTable("26B",
                    DateConverter.stringToDate("13/07/2026"),
                    DateConverter.stringToDate("06/11/2026"),
                    DateConverter.stringToDate("24/08/2026"),
                    DateConverter.stringToDate("07/09/2026")));

        });
    }

    /// Full pipeline to scrape course outlines and save it into tables
    @Transaction
    public void startCourseOutlinePipeline(HttpUrl url, RepositoryCallback callback) {
        try {
            new Thread(() -> {
                // Get the document with course outline scraper
                Document paperOutline = null;
                try {
                    paperOutline = scraper.getCourseOutline(url);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

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
                    dbController.saveStaff(staff);
                }

                // Add the assessments to the database
                ArrayList<AssessmentTable> assessments = currentOutline.getAssessments();
                for (AssessmentTable assessment : assessments) {
                    dbController.saveAssessment(assessment);
                }

                // Add the events to the database
                ArrayList<TimetablePatternTable> timetablePatterns =
                        currentOutline.getTimetablePatterns();
                for (TimetablePatternTable timetablePattern : timetablePatterns) {
                    dbController.saveTimetablePattern(timetablePattern);
                }
                // TODO: Handle error of same thread
                currentOutline.setEvents(cleaner.createEventData(cleaner.semester_fk));
                ArrayList<EventTable> events = currentOutline.getEvents();
                for (EventTable event : events) {
                    dbController.saveEvent(event);
                }
            }).start();
            // Callback for successful pipeline
           callback.OnComplete("Initial Success");
        } catch (Exception e) {
           callback.OnComplete("Error: " + e.getMessage());
        }

    }


    // Callback for pipeline
    public interface RepositoryCallback {
        void OnComplete(String result);
    }

}
