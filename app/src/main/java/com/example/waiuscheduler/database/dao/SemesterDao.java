package com.example.waiuscheduler.database.dao;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.waiuscheduler.database.tables.SemesterEntity;

import java.util.List;

/// Database object for semester table
@androidx.room.Dao
public interface SemesterDao {
    /// Insert semester details
    /// @param semester A semester occurrence
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Replace Information if duplicate
    void insert(SemesterEntity semester);

    /// Delete semester details
    /// @param semester A semester occurrence
    @Delete
    void delete(SemesterEntity semester);

    /// Update semester details
    /// @param semester A semester occurrence
    @Update
    void update(SemesterEntity semester);

    /// Delete all semesters
    @Query("DELETE FROM semester")
    void deleteAllSemesters();

    /// Select all semesters
    /// @return All semesters in table
    @Query("SELECT * FROM semester")
    List<SemesterEntity> getAllSemesters();
}
