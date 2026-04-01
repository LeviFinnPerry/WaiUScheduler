package com.example.waiuscheduler.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.waiuscheduler.database.tables.StaffEntity;

import java.util.List;

/// Database object for staff table
@androidx.room.Dao
public interface StaffDao {
    /// Insert staff details
    /// @param staff A staff members details
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Replace Information if duplicate
    void insert(StaffEntity staff);

    /// Delete staff details
    /// @param staff A staff members details
    @Delete
    void delete(StaffEntity staff);

    /// Update staff details
    /// @param staff A staff members details
    @Update
    void update(StaffEntity staff);

    /// Delete all staff
    @Query("DELETE FROM staff")
    void deleteAllStaff();

    /// Select all staff
    /// @return All staff members in the table
    @Query("SELECT * FROM staff")
    LiveData<List<StaffEntity>> getAllStaff();
}
