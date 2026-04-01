package com.example.waiuscheduler.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.waiuscheduler.database.tables.AssessmentEntity;

import java.util.List;

/// Database object for Assessment Table
@androidx.room.Dao
public interface AssessmentDao {
    /// Insert assessment details
    /// @param assessment An assessment occurance
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Replace Information if duplicate
    void insert(AssessmentEntity assessment);

    /// Delete assessment details
    /// @param assessment An assessment occurance
    @Delete
    void delete(AssessmentEntity assessment);

    /// Update assessment details
    /// @param assessment An assessment occurance
    @Update
    void update(AssessmentEntity assessment);

    /// Delete all assessments
    @Query("DELETE FROM assessment")
    void deleteAllAssessments();

    /// Select all assessments
    /// @return All assessments in the table
    @Query("SELECT * FROM assessment")
    LiveData<List<AssessmentEntity>> getAllAssessments();
}
