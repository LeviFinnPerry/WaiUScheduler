package com.example.waiuscheduler.parsing;

import android.content.Context;

import com.example.waiuscheduler.database.AppDatabase;
import com.example.waiuscheduler.database.DatabaseController;
import com.example.waiuscheduler.database.tables.PaperTable;
import com.example.waiuscheduler.database.tables.SemesterTable;
import com.example.waiuscheduler.database.tables.StaffTable;

import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.concurrent.Executors;

import okhttp3.HttpUrl;

public class DataRepository {
    private final CourseOutlineScraper scraper;
    private final DataCleaner cleaner;
    private final AppDatabase db;

    private final DatabaseController dbController;

    public DataRepository(Context context) {
        this.scraper = new CourseOutlineScraper();
        this.cleaner = new DataCleaner();
        this.db = AppDatabase.getInstance(context);
        this.dbController = new DatabaseController(db);

        AppDatabase.databaseWriteExecutor.execute(() -> {
            // Add the dates
            // TODO: Update from strings to dates
            dbController.saveSemester(new SemesterTable("26A", "02/03/2026", "26/06/2026", "08/04/2026", "20/04/2026"));
            dbController.saveSemester(new SemesterTable("26B", "13/07/2026", "06/11/2026", "24/08/2026", "07/09/2026"));

        });
    }

    /// Full pipeline to scrape course outlines and save it into tables
    public void startCourseOutlinePipeline(HttpUrl url, RepositoryCallback callback) {
        new Thread(() -> {
           try {
               // Get the document with course outline scraper
               Document paperOutline = scraper.getCourseOutline(url);

               // Clean the data
               ScrapedData cleanOutline = cleaner.clean(paperOutline);

               // Write the data to the database

                   // Add the paper information to database
                   PaperTable paper = cleanOutline.getPaper();
                   dbController.savePaper(paper);

                   // Add the staff members to the database
                   ArrayList<StaffTable> staffMembers = cleanOutline.getStaff();
                   // Set the paperId as foreign key
                   for (StaffTable staff: staffMembers) {

                       dbController.saveStaff(staff);
                   }

                // Callback for successful pipeline
                callback.OnComplete("Success");
           } catch (Exception e) {
               callback.OnComplete("Error: " + e.getMessage());
           }
        }).start();

    }

    // Callback for pipeline
    public interface RepositoryCallback {
        void OnComplete(String result);
    }
}
