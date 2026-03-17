package com.example.waiuscheduler;

import android.app.Application;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.waiuscheduler.dao.AssessmentDao;
import com.example.waiuscheduler.dao.EventDao;
import com.example.waiuscheduler.dao.PaperDao;
import com.example.waiuscheduler.dao.SemesterDao;
import com.example.waiuscheduler.dao.StaffDao;
import com.example.waiuscheduler.dao.StudySessionDao;
import com.example.waiuscheduler.dao.TimetablePatternDao;

import com.example.waiuscheduler.database.AssessmentTable;
import com.example.waiuscheduler.database.EventTable;
import com.example.waiuscheduler.database.PaperTable;
import com.example.waiuscheduler.database.SemesterTable;
import com.example.waiuscheduler.database.StaffTable;
import com.example.waiuscheduler.database.StudySessionTable;
import com.example.waiuscheduler.database.TimetablePatternTable;

@Database(entities = {
        AssessmentTable.class,
        EventTable.class,
        PaperTable.class,
        SemesterTable.class,
        StaffTable.class,
        StudySessionTable.class,
        TimetablePatternTable.class
}, version = 1)
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
    private static DatabaseController dbController;

    // Get instance of the database
    public static AppDatabase getInstance(Context context) {
        // If not already an instance
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {  // Check the class
                if (INSTANCE == null) {         // If still no instance
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "app_database")
                            .enableMultiInstanceInvalidation()  // Allow for foreign keys
                            .fallbackToDestructiveMigration() // Handle migrations
                            .build();

                    dbController = new DatabaseController(context);
                }
            }
        }
        return INSTANCE;
    }

    // Get the database controller
    public static DatabaseController getDatabaseController() {
        if (dbController != null) {
            return dbController;
        } else {
            return null;
        }
    }



}
