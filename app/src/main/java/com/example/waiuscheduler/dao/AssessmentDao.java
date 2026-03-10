package com.example.waiuscheduler.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.waiuscheduler.database.AssessmentTable;

import java.util.List;

// Database object for Assessment Table
@androidx.room.Dao
public interface AssessmentDao {
    // Add data
    @Insert
    void insert(AssessmentTable assessment);

    // Remove data
    @Delete
    void delete(AssessmentTable assessment);

    // Update data
    @Update
    void update(AssessmentTable assessment);

    // Delete all assessments
    @Query("DELETE FROM assessment")
    void deleteAllAssessments();

    // Select all assessments
    @Query("SELECT * FROM assessment")
    LiveData<List<AssessmentTable>> getAllAssessments();
}
