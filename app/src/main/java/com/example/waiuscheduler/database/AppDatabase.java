package com.example.waiuscheduler.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/// Database for each table
@Database(entities = {
        AssessmentTable.class,
        EventTable.class,
        PaperTable.class,
        SemesterTable.class,
        StaffTable.class,
        StudySessionTable.class,
        TimetablePatternTable.class
}, version = 9)
@TypeConverters({DateConverter.class})  // Type converter for dates
public abstract class AppDatabase extends RoomDatabase {
    public abstract AssessmentDao assessmentDao();  // Assessment database object
    public abstract EventDao eventDao();    // Event database object
    public abstract PaperDao paperDao();    // Paper database object
    public abstract SemesterDao semesterDao();  // Semester database object
    public abstract StaffDao staffDao();    // Staff database object
    public abstract StudySessionDao studySessionDao();  // Study session database object
    public abstract TimetablePatternDao timetablePatternDao();  // Timetable pattern database object


    // Instance for the database
    private static volatile AppDatabase INSTANCE;

    // Fixed thread executor
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(8);

    /// Initialise instance of the database
    public static AppDatabase getInstance(final Context context) {
        // If not already an instance
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {  // Check the class
                if (INSTANCE == null) {         // If still no instance
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "app_database")
                            .enableMultiInstanceInvalidation()  // Allow for foreign keys
                            .fallbackToDestructiveMigration() // Handle migrations
                            .addCallback(initialiseDB)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /// Add semester dates for the year manually at the beginning
    private static final RoomDatabase.Callback initialiseDB = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            db.execSQL("PRAGMA foreign_keys=ON");   // Enforce foreign keys
        }
    };
}
