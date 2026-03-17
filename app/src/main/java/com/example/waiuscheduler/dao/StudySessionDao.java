package com.example.waiuscheduler.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.waiuscheduler.database.StudySessionTable;

import java.util.List;

@androidx.room.Dao
public interface StudySessionDao {
    // Add data
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Replace Information if duplicate
    void insert(StudySessionTable session);

    // Remove data
    @Delete
    void delete(StudySessionTable session);

    // Update data
    @Update
    void update(StudySessionTable session);

    // Delete all sessions
    @Query("DELETE FROM study_session")
    void deleteAllSessions();

    // Select all sessions
    @Query("SELECT * FROM study_session")
    LiveData<List<StudySessionTable>> getAllSessions();
}
