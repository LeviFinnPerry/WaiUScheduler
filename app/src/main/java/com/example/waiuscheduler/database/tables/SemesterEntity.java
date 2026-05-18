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
    private final Date startDate;       // Start Date for the semester
    private final Date endDate;         // End Date for the semester
    private final Date breakStartDate;  // Start date for the mid semester break
    private final Date breakEndDate;    // End date for the mid semester break

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

    /// Get date semester starts
    /// @return Start date of the semester
    public Date getStartDate() {
        return startDate;
    }

    /// Get date semester ends
    /// @return End date of the semester
    public Date getEndDate() {
        return endDate;
    }

    /// Get date semester break starts
    /// @return Start date of the semester break
    public Date getBreakStartDate() {
        return breakStartDate;
    }

    /// Get date semester break ends
    /// @return End date of the semester break
    public Date getBreakEndDate() {
        return breakEndDate;
    }
}
