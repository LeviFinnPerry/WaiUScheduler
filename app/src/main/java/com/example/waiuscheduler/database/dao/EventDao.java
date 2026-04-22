package com.example.waiuscheduler.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.waiuscheduler.database.tables.EventEntity;

import java.util.List;

/// Database object for the event table
@androidx.room.Dao
public interface EventDao {
    /// Insert event details
    /// @param event An event occurance
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Replace Information if duplicate
    void insert(EventEntity event);

    /// Delete event details
    /// @param event An event occurance
    @Delete
    void delete(EventEntity event);

    /// Update event details
    /// @param event An event occurance
    @Update
    void update(EventEntity event);

    /// Select all events
    /// @return All events in the table
    @Query("SELECT * FROM event")
    LiveData<List<EventEntity>> getAllEvents();


    /// Returns all events within start and end date
    /// @param start Range start
    /// @param end Range end
    @Query("SELECT * FROM event " +
            "WHERE dateTimeStart <= :end AND dateTimeEnd >= :start " +
            "ORDER BY dateTimeStart ASC")
    LiveData<List<EventEntity>> getEventsInRange(long start, long end);
}
