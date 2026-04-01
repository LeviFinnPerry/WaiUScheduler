package com.example.waiuscheduler.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.waiuscheduler.database.tables.StudySessionEntity;

import java.util.List;

/// Database object for study session table
@androidx.room.Dao
public interface StudySessionDao {
    /// Insert study session details
    /// @param session A study session occurrence
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Replace Information if duplicate
    void insert(StudySessionEntity session);

    /// Delete study session details
    /// @param session A study session occurrence
    @Delete
    void delete(StudySessionEntity session);

    /// Update study session details
    /// @param session A study session occurrence
    @Update
    void update(StudySessionEntity session);

    /// Delete all study sessions
    @Query("DELETE FROM study_session")
    void deleteAllSessions();

    /// Select all study sessions
    /// @return All study sessions in table
    @Query("SELECT * FROM study_session")
    LiveData<List<StudySessionEntity>> getAllSessions();
}
