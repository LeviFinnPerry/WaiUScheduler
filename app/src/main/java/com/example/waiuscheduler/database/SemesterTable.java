package com.example.waiuscheduler.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "semester")
public class SemesterTable {
    @PrimaryKey @NonNull
    private String semesterCode;    // Unique occurrence code for each semester eg. 26A
    private String startDate;       // Start Date for the semester
    private String endDate;         // End Date for the semester
    private String breakStartDate;  // Start date for the mid semester break
    private String breakEndDate;    // End date for the mid semester break

    // Constructor for the semester table
    public SemesterTable(String semesterCode, String startDate,
                         String endDate, String breakStartDate, String breakEndDate) {
        this.semesterCode = semesterCode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.breakStartDate = breakStartDate;
        this.breakEndDate = breakEndDate;
    }

    // Getter and setter for each variable
    // Semester code
    public String getSemesterCode() {
        return semesterCode;
    }

    public void setSemesterCode(String semesterCode) {
        this.semesterCode = semesterCode;
    }

    // Start date
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    // End date
    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    // Break Start Date
    public String getBreakStartDate() {
        return breakStartDate;
    }

    public void setBreakEndDate(String breakEndDate) {
        this.breakEndDate = breakEndDate;
    }

    // Break End Date
    public String getBreakEndDate() {
        return breakEndDate;
    }

    public void setBreakStartDate(String breakStartDate) {
        this.breakStartDate = breakStartDate;
    }
}
