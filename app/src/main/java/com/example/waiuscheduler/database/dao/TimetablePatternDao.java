package com.example.waiuscheduler.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.waiuscheduler.database.tables.TimetablePatternEntity;

import java.util.List;

/// Database object for timetable pattern table
@androidx.room.Dao
public interface TimetablePatternDao {
    /// Insert timetable pattern occurrence
    /// @param timetable A timetable pattern occurrence
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Replace Information if duplicate
    void insert(TimetablePatternEntity timetable);

    /// Delete timetable pattern occurrence
    /// @param timetable A timetable pattern occurrence
    @Delete
    void delete(TimetablePatternEntity timetable);

    /// Update timetable pattern occurrence
    /// @param timetable A timetable pattern occurrence
    @Update
    void update(TimetablePatternEntity timetable);

    /// Delete all timetable patterns
    @Query("DELETE FROM timetable_pattern")
    void deleteAllTimetables();

    /// Select all timetable patterns
    /// @return All timetable patterns in the table
    @Query("SELECT * FROM timetable_pattern")
    LiveData<List<TimetablePatternEntity>> getAllTimetables();
}
