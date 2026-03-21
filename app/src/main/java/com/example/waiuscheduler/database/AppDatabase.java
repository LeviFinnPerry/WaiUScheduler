package com.example.waiuscheduler.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.waiuscheduler.dao.AssessmentDao;
import com.example.waiuscheduler.dao.EventDao;
import com.example.waiuscheduler.dao.PaperDao;
import com.example.waiuscheduler.dao.SemesterDao;
import com.example.waiuscheduler.dao.StaffDao;
import com.example.waiuscheduler.dao.StudySessionDao;
import com.example.waiuscheduler.dao.TimetablePatternDao;

import com.example.waiuscheduler.database.tables.AssessmentTable;
import com.example.waiuscheduler.database.tables.EventTable;
import com.example.waiuscheduler.database.tables.PaperTable;
import com.example.waiuscheduler.database.tables.SemesterTable;
import com.example.waiuscheduler.database.tables.StaffTable;
import com.example.waiuscheduler.database.tables.StudySessionTable;
import com.example.waiuscheduler.database.tables.TimetablePatternTable;

import java.util.concurrent.Executors;

@Database(entities = {
        AssessmentTable.class,
        EventTable.class,
        PaperTable.class,
        SemesterTable.class,
        StaffTable.class,
        StudySessionTable.class,
        TimetablePatternTable.class
}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AssessmentDao assessmentDao();
    public abstract EventDao eventDao();
    public abstract PaperDao paperDao();
    public abstract SemesterDao semesterDao();
    public abstract StaffDao staffDao();
    public abstract StudySessionDao studySessionDao();
    public abstract TimetablePatternDao timetablePatternDao();

    // Instance for the database
    private static volatile AppDatabase INSTANCE;

    // Get instance of the database
    public static AppDatabase getInstance(final Context context) {
        // If not already an instance
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {  // Check the class
                if (INSTANCE == null) {         // If still no instance
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "app_database")
                            .enableMultiInstanceInvalidation()  // Allow for foreign keys
                            .fallbackToDestructiveMigration() // Handle migrations
                            .addCallback(insertSemesterDates)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Add semester dates for the year manually at the beginning
    private static final RoomDatabase.Callback insertSemesterDates = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            Executors.newSingleThreadScheduledExecutor().execute(() -> {
                SemesterDao semester = INSTANCE.semesterDao();

                // Add the dates
                // TODO: Update from strings to dates
                semester.insert(new SemesterTable("26A", "02/03/2026", "26/06/2026", "08/04/2026", "20/04/2026"));
                semester.insert(new SemesterTable("26B", "13/07/2026", "06/11/2026", "24/08/2026", "07/09/2026"));
            });
        }
    };
}
