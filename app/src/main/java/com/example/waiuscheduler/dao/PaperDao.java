package com.example.waiuscheduler.dao;

import androidx.lifecycle.LiveData;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.waiuscheduler.database.tables.PaperEntity;

import java.util.List;

/// Database object for paper table
@androidx.room.Dao
public interface PaperDao {
    /// Insert paper details
    /// @param paper A paper occurrence
    @Insert // Replace Information if duplicate
    void insert(PaperEntity paper);

    /// Delete paper details
    /// @param paper A paper occurrence
    @Delete
    void delete(PaperEntity paper);

    /// Update paper details
    /// @param paper A paper occurrence
    @Update
    void update(PaperEntity paper);

    /// Delete all papers
    @Query("DELETE FROM paper")
    void deleteAllPapers();

    /// Select all papers
    /// @return All papers in the table
    @Query("SELECT * FROM paper")
    LiveData<List<PaperEntity>> getAllPapers();
}
