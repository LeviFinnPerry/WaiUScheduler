package com.example.waiuscheduler.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.waiuscheduler.database.StaffTable;

import java.util.List;

@androidx.room.Dao
public interface StaffDao {
    // Add data
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Replace Information if duplicate
    void insert(StaffTable staff);

    // Remove data
    @Delete
    void delete(StaffTable staff);

    // Update data
    @Update
    void update(StaffTable staff);

    // Delete all staff
    @Query("DELETE FROM staff")
    void deleteAllStaff();

    // Select all staff
    @Query("SELECT * FROM staff")
    LiveData<List<StaffTable>> getAllStaff();
}
