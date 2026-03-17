package com.example.waiuscheduler.dao;

import androidx.lifecycle.LiveData;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.waiuscheduler.database.PaperTable;

import java.util.List;

// Database object for paper table
@androidx.room.Dao
public interface PaperDao {
    // Add data
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Replace Information if duplicate
    void insert(PaperTable paper);

    // Remove data
    @Delete
    void delete(PaperTable paper);

    // Update data
    @Update
    void update(PaperTable paper);

    // Delete all papers
    @Query("DELETE FROM paper")
    void deleteAllPapers();

    // Select all papers
    @Query("SELECT * FROM paper")
    LiveData<List<PaperTable>> getAllPapers();
}
