package com.example.waiuscheduler.database.tables;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

/// Entity for timetable pattern table
@Entity(tableName = "timetable_pattern",
        foreignKeys = @ForeignKey(
                entity = PaperEntity.class,      // Class of the foreign key
                parentColumns = "paperId",      // Foreign key variable
                childColumns = "paperId_fk",    // Foreign key name
                onDelete = ForeignKey.CASCADE   // Dependency on paper
        ),
        indices = {@Index("paperId_fk")}
)
public class TimetablePatternEntity {
    @PrimaryKey @NonNull
    private String type;            // Type of timetable event (eg. lecture)
    private int dayOfWeek;       // Day of week event occurs on
    private Date startTime;       // Start time
    private Date endTime;         // End time
    private String location;        // Location for event
    private Double duration;        // Total duration in hours
    private String paperId_fk;     // Foreign key references event table

    /// Constructor for the timetable table
    /// @param type Type of scheduled timetable occurrence
    /// @param dayOfWeek Number alternative of the day of the week
    /// @param startTime Time the timetable occurrence started
    /// @param endTime Time the timetable occurrence ended
    /// @param location Building the timetable occurrence is in
    /// @param duration Duration of the timetable occurrence
    /// @param paperId_fk Foreign key to the paper id the timetable is from
    public TimetablePatternEntity(
            @NonNull String type, int dayOfWeek, Date startTime,
            Date endTime, String location, Double duration, String paperId_fk
    ) {
        this.type = type;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.duration = duration;
        this.paperId_fk = paperId_fk;
    }

    /// Get the type of timetable pattern occurrence
    /// @return The type of timetable occurrence
    @NonNull
    public String getType() {
        return type;
    }

    /// Set the type of timetable pattern occurrence
    /// @param type The type of timetable occurrence
    public void setType(@NonNull String type) {
        this.type = type;
    }

    /// Get the day of the week for the timetable pattern
    /// @return Numeric representation for the day of the week
    public int getDayOfWeek() {
        return dayOfWeek;
    }

    /// Set the day of the week for the timetable pattern
    /// @param dayOfWeek Numeric representation for the day of the week
    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    /// Get the occurrence start time
    /// @return Time the timetable pattern starts
    public Date getStartTime() {
        return startTime;
    }

    /// Set the occurrence start time
    /// @param startTime Time the timetable pattern starts
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /// Get the occurrence end time
    /// @return Time the timetable pattern ends
    public Date getEndTime() {
        return endTime;
    }

    /// Set the occurrence start time
    /// @param endTime Time the timetable pattern ends
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /// Get the location of the timetable pattern
    /// @return Building the occurrence is in
    public String getLocation() {
        return location;
    }

    /// Set the location of the timetable pattern
    /// @param location Building the occurrence is in
    public void setLocation(String location) {
        this.location = location;
    }

    /// Get the duration of the timetable pattern
    /// @return Length of duration in hours
    public Double getDuration() {
        return duration;
    }

    /// Set the duration of the timetable pattern
    /// @param duration Length of occurrence in hours
    public void setDuration(Double duration) {
        this.duration = duration;
    }

    /// Get the paper id that corresponds to the timetable occurrence
    /// @return Foreign key paper id the timetable pattern is from
    public String getPaperId_fk() {
        return paperId_fk;
    }

    /// Set the paper id that corresponds to the timetable occurrence
    /// @param paperId_fk Foreign key paper id the timetable pattern is from
    public void setPaperId_fk(String paperId_fk) {
        this.paperId_fk = paperId_fk;
    }
}
