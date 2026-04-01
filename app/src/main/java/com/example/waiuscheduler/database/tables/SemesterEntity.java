package com.example.waiuscheduler.database.tables;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

/// Entity for semester table
@Entity(tableName = "semester")
public class SemesterEntity {
    @PrimaryKey @NonNull
    private String semesterCode;    // Unique occurrence code for each semester eg. 26A
    private Date startDate;       // Start Date for the semester
    private Date endDate;         // End Date for the semester
    private Date breakStartDate;  // Start date for the mid semester break
    private Date breakEndDate;    // End date for the mid semester break

    /// Constructor for the semester table
    /// @param semesterCode The occurrence code of the semester
    /// @param startDate The date the semester starts
    /// @param endDate The date the semester ends
    /// @param breakStartDate The date the break starts
    /// @param breakEndDate The date the break ends
    public SemesterEntity(
            @NonNull String semesterCode, Date startDate,
            Date endDate, Date breakStartDate, Date breakEndDate) {
        this.semesterCode = semesterCode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.breakStartDate = breakStartDate;
        this.breakEndDate = breakEndDate;
    }

    /// Get semester code
    /// @return The occurrence code of the semester
    @NonNull
    public String getSemesterCode() {
        return semesterCode;
    }

    /// Set semester code
    /// @param semesterCode The occurrence code of the semester
    public void setSemesterCode(@NonNull String semesterCode) {
        this.semesterCode = semesterCode;
    }

    /// Get date semester starts
    /// @return Start date of the semester
    public Date getStartDate() {
        return startDate;
    }

    /// Set date semester starts
    /// @param startDate Start date of the semester
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /// Get date semester ends
    /// @return End date of the semester
    public Date getEndDate() {
        return endDate;
    }

    /// Set date semester ends
    /// @param endDate End date of the semester
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /// Get date semester break starts
    /// @return Start date of the semester break
    public Date getBreakStartDate() {
        return breakStartDate;
    }

    /// Set date semester break starts
    /// @param breakStartDate Start date of the semester break
    public void setBreakStartDate(Date breakStartDate) {
        this.breakStartDate = breakStartDate;
    }

    /// Set date semester break ends
    /// @param breakEndDate End date of the semester break
    public void setBreakEndDate(Date breakEndDate) {
        this.breakEndDate = breakEndDate;
    }

    /// Get date semester break ends
    /// @return End date of the semester break
    public Date getBreakEndDate() {
        return breakEndDate;
    }
}
