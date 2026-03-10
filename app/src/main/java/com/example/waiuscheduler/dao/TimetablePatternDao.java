package com.example.waiuscheduler.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.waiuscheduler.database.TimetablePatternTable;

import java.util.List;

@androidx.room.Dao
public interface TimetablePatternDao {
    // Add data
    @Insert
    void insert(TimetablePatternTable timetable);

    // Remove data
    @Delete
    void delete(TimetablePatternTable timetable);

    // Update data
    @Update
    void update(TimetablePatternTable timetable);

    // Delete all timetables
    @Query("DELETE FROM timetable_pattern")
    void deleteAllTimetables();

    // Select all timetables
    @Query("SELECT * FROM timetable_pattern")
    LiveData<List<TimetablePatternTable>> getAllTimetables();
}
