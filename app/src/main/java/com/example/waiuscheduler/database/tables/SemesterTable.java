package com.example.waiuscheduler.database.tables;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

/// Entity for semester table
@Entity(tableName = "semester")
public class SemesterTable {
    @PrimaryKey @NonNull
    private String semesterCode;    // Unique occurrence code for each semester eg. 26A
    private Date startDate;       // Start Date for the semester
    private Date endDate;         // End Date for the semester
    private Date breakStartDate;  // Start date for the mid semester break
    private Date breakEndDate;    // End date for the mid semester break

    /// Constructor for the semester table
    public SemesterTable(
            @NonNull String semesterCode, Date startDate,
            Date endDate, Date breakStartDate, Date breakEndDate) {
        this.semesterCode = semesterCode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.breakStartDate = breakStartDate;
        this.breakEndDate = breakEndDate;
    }

    /// Getter and setter for each variable
    // Semester code
    @NonNull
    public String getSemesterCode() {
        return semesterCode;
    }
    public void setSemesterCode(@NonNull String semesterCode) {
        this.semesterCode = semesterCode;
    }

    // Start date
    public Date getStartDate() {
        return startDate;
    }
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    // End date
    public Date getEndDate() {
        return endDate;
    }
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    // Break Start Date
    public Date getBreakStartDate() {
        return breakStartDate;
    }
    public void setBreakEndDate(Date breakEndDate) {
        this.breakEndDate = breakEndDate;
    }

    // Break End Date
    public Date getBreakEndDate() {
        return breakEndDate;
    }
    public void setBreakStartDate(Date breakStartDate) {
        this.breakStartDate = breakStartDate;
    }
}
