package com.example.waiuscheduler.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.waiuscheduler.database.tables.EventTable;

import java.util.List;

// Database object for the event table
@androidx.room.Dao
public interface EventDao {
    // Add data
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Replace Information if duplicate
    void insert(EventTable event);

    // Remove data
    @Delete
    void delete(EventTable event);

    // Update data
    @Update
    void update(EventTable event);

    // Delete all events
    @Query("DELETE FROM event")
    void deleteAllEvents();

    // Select all events
    @Query("SELECT * FROM event")
    LiveData<List<EventTable>> getAllEvents();
}
