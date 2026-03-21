package com.example.waiuscheduler.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.waiuscheduler.database.tables.SemesterTable;

import java.util.List;

@androidx.room.Dao
public interface SemesterDao {
    // Add data
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Replace Information if duplicate
    void insert(SemesterTable semester);

    // Remove data
    @Delete
    void delete(SemesterTable semester);

    // Update data
    @Update
    void update(SemesterTable semester);

    // Delete all semesters
    @Query("DELETE FROM semester")
    void deleteAllSemesters();

    // Select all semesters
    @Query("SELECT * FROM semester")
    LiveData<List<SemesterTable>> getAllSemesters();
}
